import Foundation
import SwiftUI
import Combine

final class TeacherViewModel: ObservableObject {
    
    @Published var currentLesson: TeacherLesson? = nil
    @Published var teacherProfile: TeacherProfileResponse? = nil
    @Published var todayLessons: [TeacherLesson] = []
    @Published var groupSubjects: [String] = []
    @Published var groupStudents: [GroupStudent] = []
    @Published var weeklyGrades: [WeeklyGradeItem] = []
    @Published var activeLesson: GroupActiveLesson? = nil
    @Published var attendance: [LessonAttendance] = []
    
    private var pollingTask: Task<Void, Never>? = nil
    
    func loadTeacherProfile() {
        let teacherId = UserDefaults.standard.integer(forKey: "userId")
        guard teacherId > 0 else { return }
        
        APIService.shared.getTeacherProfile(teacherId: teacherId) { [weak self] result in
            switch result {
            case .success(let profile):
                self?.teacherProfile = profile
                
                UserDefaults.standard.set(
                    profile.teacher.full_name,
                    forKey: "fullName"
                )
                UserDefaults.standard.set(
                    profile.curated_group,
                    forKey: "curatedGroup"
                )
                
            case .failure(let error):
                print("Ошибка загрузки профиля: \(error)")
            }
        }
    }
    
    func loadTeacherProfileIfNeeded() {
        if teacherProfile == nil {
            loadTeacherProfile()
        }
    }
    
    func loadCurrentLesson() {
        let teacherId = UserDefaults.standard.integer(forKey: "userId")
        guard teacherId > 0 else { return }
        
        APIService.shared.getCurrentTeacherLesson(teacherId: teacherId) { [weak self] result in
            switch result {
            case .success(let lesson):
                self?.currentLesson = lesson
            case .failure(let error):
                print("Ошибка загрузки урока: \(error)")
                self?.currentLesson = nil
            }
        }
    }
    
    func loadTodayLessons() {
        let teacherId = UserDefaults.standard.integer(forKey: "userId")
        guard teacherId > 0 else { return }
        
        APIService.shared.getTodayTeacherLessons(teacherId: teacherId) { [weak self] result in
            switch result {
            case .success(let lessons):
                self?.todayLessons = lessons
            case .failure(let error):
                print("Ошибка загрузки уроков на сегодня: \(error)")
                self?.todayLessons = []
            }
        }
    }
    
    func startPolling() {
        stopPolling()
        
        pollingTask = Task { [weak self] in
            guard let self else { return }
            
            while !Task.isCancelled {
                await MainActor.run {
                    self.loadCurrentLesson()
                }
                
                try? await Task.sleep(nanoseconds: 10_000_000_000)
            }
        }
    }
    
    func stopPolling() {
        pollingTask?.cancel()
        pollingTask = nil
    }

    func loadGroupSubjects(groupName: String) {
        APIService.shared.getSubjectsByGroup(groupName: groupName) { [weak self] result in
            switch result {
            case .success(let list): self?.groupSubjects = list
            case .failure(let error):
                print("Ошибка предметов: \(error)")
                self?.groupSubjects = []
            }
        }
    }

    func loadGroupStudents(groupName: String) {
        APIService.shared.getGroupStudents(groupName: groupName) { [weak self] result in
            switch result {
            case .success(let list): self?.groupStudents = list
            case .failure(let error):
                print("Ошибка студентов: \(error)")
                self?.groupStudents = []
            }
        }
    }

    func loadWeeklyGrades(groupName: String, subject: String, startDate: String) {
        APIService.shared.getWeeklyGrades(
            groupName: groupName,
            subject: subject,
            startDate: startDate
        ) { [weak self] result in
            switch result {
            case .success(let list): self?.weeklyGrades = list
            case .failure(let error):
                print("Ошибка оценок: \(error)")
                self?.weeklyGrades = []
            }
        }
    }

    func checkGroupActiveLesson(groupName: String) {
        APIService.shared.getGroupActiveLesson(groupName: groupName) { [weak self] result in
            switch result {
            case .success(let lesson): self?.activeLesson = lesson
            case .failure: self?.activeLesson = nil
            }
        }
    }

    func loadAttendance(lessonId: Int) {
        APIService.shared.getLessonAttendance(lessonId: lessonId) { [weak self] result in
            switch result {
            case .success(let list): self?.attendance = list
            case .failure(let error):
                print("Ошибка посещаемости: \(error)")
                self?.attendance = []
            }
        }
    }

    func setGrade(
        lessonId: Int,
        studentId: Int,
        grade: Int,
        completion: @escaping () -> Void
    ) {
        APIService.shared.setGrade(
            lessonId: lessonId,
            studentId: studentId,
            grade: grade,
            attendance: true
        ) { result in
            switch result {
            case .success: completion()
            case .failure(let error): print("Ошибка сохранения: \(error)")
            }
        }
    }

    func flushPendingGrades(lessonId: Int, completion: (() -> Void)? = nil) {
        let pending = GradeStorage.shared.load(lessonId: lessonId)
        guard !pending.isEmpty else {
            completion?()
            return
        }
        
        let group = DispatchGroup()
        
        for (studentIdStr, grade) in pending {
            guard let studentId = Int(studentIdStr) else { continue }
            
            group.enter()
            APIService.shared.setGrade(
                lessonId: lessonId,
                studentId: studentId,
                grade: grade,
                attendance: true
            ) { _ in
                group.leave()
            }
        }
        
        group.notify(queue: .main) {
            GradeStorage.shared.clear(lessonId: lessonId)
            completion?()
        }
    }
}
