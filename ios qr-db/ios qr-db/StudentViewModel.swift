import Foundation
import SwiftUI
import Combine

final class StudentViewModel: ObservableObject {
    
    @Published var profile: StudentProfileResponse? = nil
    
    func loadProfile() {
        let studentId = UserDefaults.standard.integer(forKey: "userId")
        guard studentId > 0 else { return }
        
        APIService.shared.getStudentProfile(studentId: studentId) { [weak self] result in
            switch result {
            case .success(let profile):
                self?.profile = profile
                
                UserDefaults.standard.set(
                    profile.student.full_name,
                    forKey: "fullName"
                )
                UserDefaults.standard.set(
                    profile.student.group_name,
                    forKey: "groupName"
                )
                
            case .failure(let error):
                print("Ошибка загрузки профиля студента: \(error)")
            }
        }
    }
    
    func loadProfileIfNeeded() {
        if profile == nil {
            loadProfile()
        }
    }
    
    @Published var todaySchedule: [StudentScheduleItem] = []

    func loadTodaySchedule() {
        let studentId = UserDefaults.standard.integer(forKey: "userId")
        guard studentId > 0 else { return }
        
        APIService.shared.getStudentSchedule(studentId: studentId) { [weak self] result in
            switch result {
            case .success(let list):
                
                let formatter = DateFormatter()
                formatter.dateFormat = "yyyy-MM-dd"
                let todayStr = formatter.string(from: Date())
                
                let todayOnly = list.filter { item in
                    if let d = item.date {
                        return d.prefix(10) == todayStr
                    }
                    if let s = item.start_time {
                        return s.prefix(10) == todayStr
                    }
                    return false
                }
                
                self?.todaySchedule = todayOnly
                
            case .failure(let error):
                print("Ошибка расписания студента: \(error)")
                self?.todaySchedule = []
            }
        }
    }

    @Published var weeklyGrades: [StudentWeeklyGradeItem] = []

    func loadWeeklyGrades(startDate: String) {
        let studentId = UserDefaults.standard.integer(forKey: "userId")
        guard studentId > 0 else { return }
        
        APIService.shared.getStudentWeeklyGrades(
            studentId: studentId,
            startDate: startDate
        ) { [weak self] result in
            switch result {
            case .success(let list):
                self?.weeklyGrades = list
            case .failure(let error):
                print("Ошибка журнала студента: \(error)")
                self?.weeklyGrades = []
            }
        }
    }
}
