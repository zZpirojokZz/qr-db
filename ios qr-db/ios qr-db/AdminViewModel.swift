import Foundation
import SwiftUI
import Combine

final class AdminViewModel: ObservableObject {
    
    @Published var currentLesson: TeacherLesson? = nil
    @Published var teacherProfile: TeacherProfileResponse? = nil
    @Published var todayLessons: [TeacherLesson] = []
    
    @Published var groupSubjects: [String] = []
    @Published var groupStudents: [GroupStudent] = []
    @Published var weeklyGrades: [WeeklyGradeItem] = []
    @Published var attendance: [LessonAttendance] = []
    
    private var pollingTask: Task<Void, Never>? = nil
    
    func loadProfile() {
        let id = UserDefaults.standard.integer(forKey: "userId")
        guard id > 0 else { return }
        
        APIService.shared.getTeacherProfile(teacherId: id) { [weak self] result in
            switch result {
            case .success(let profile):
                self?.teacherProfile = profile
                UserDefaults.standard.set(profile.teacher.full_name, forKey: "fullName")
                UserDefaults.standard.set(profile.curated_group, forKey: "curatedGroup")
            case .failure(let error):
                print("Ошибка профиля админа: \(error)")
            }
        }
    }
    
    func loadProfileIfNeeded() {
        if teacherProfile == nil {
            loadProfile()
        }
    }
    
    func loadCurrentLesson() {
        let id = UserDefaults.standard.integer(forKey: "userId")
        guard id > 0 else { return }
        
        APIService.shared.getCurrentTeacherLesson(teacherId: id) { [weak self] result in
            switch result {
            case .success(let lesson):
                self?.currentLesson = lesson
            case .failure:
                self?.currentLesson = nil
            }
        }
    }
    
    func loadTodayLessons() {
        let id = UserDefaults.standard.integer(forKey: "userId")
        guard id > 0 else { return }
        
        APIService.shared.getTodayTeacherLessons(teacherId: id) { [weak self] result in
            switch result {
            case .success(let lessons): self?.todayLessons = lessons
            case .failure: self?.todayLessons = []
            }
        }
    }
    
    func loadGroupSubjects(groupName: String) {
        APIService.shared.getSubjectsByGroup(groupName: groupName) { [weak self] result in
            switch result {
            case .success(let list): self?.groupSubjects = list
            case .failure: self?.groupSubjects = []
            }
        }
    }
    
    func loadGroupStudents(groupName: String) {
        APIService.shared.getGroupStudents(groupName: groupName) { [weak self] result in
            switch result {
            case .success(let list): self?.groupStudents = list
            case .failure: self?.groupStudents = []
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
            case .failure: self?.weeklyGrades = []
            }
        }
    }
    
    func loadAttendance(lessonId: Int) {
        APIService.shared.getLessonAttendance(lessonId: lessonId) { [weak self] result in
            switch result {
            case .success(let list): self?.attendance = list
            case .failure: self?.attendance = []
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
        ) { _ in
            completion()
        }
    }
    
    func updateLocalGrade(studentId: Int, grade: Int) {
        var updated = attendance
        if let index = updated.firstIndex(where: { $0.user_id == studentId }) {
            let old = updated[index]
            updated[index] = LessonAttendance(
                user_id: old.user_id,
                full_name: old.full_name,
                attendance: true,
                grade: grade
            )
        } else {
            updated.append(LessonAttendance(
                user_id: studentId,
                full_name: nil,
                attendance: true,
                grade: grade
            ))
        }
        self.attendance = updated
    }
    
    func updateLocalWeeklyGrade(studentId: Int, lessonDate: String, grade: Int) {
        var updated = weeklyGrades
        
        if let index = updated.firstIndex(where: {
            $0.student_id == studentId &&
            $0.lesson_date.prefix(10) == lessonDate.prefix(10)
        }) {
            let old = updated[index]
            updated[index] = WeeklyGradeItem(
                student_id: old.student_id,
                full_name: old.full_name,
                grade: grade,
                attendance: true,
                lesson_date: old.lesson_date,
                lesson_id: old.lesson_id
            )
        }
        
        self.weeklyGrades = updated
    }
}
