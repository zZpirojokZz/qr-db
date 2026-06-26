import Foundation

final class GradeStorage {
    
    static let shared = GradeStorage()
    
    private init() {}
    
    private func key(for lessonId: Int) -> String {
        "pendingGrades_\(lessonId)"
    }
    
    func save(lessonId: Int, studentId: Int, grade: Int) {
        var dict = load(lessonId: lessonId)
        dict[String(studentId)] = grade
        UserDefaults.standard.set(dict, forKey: key(for: lessonId))
    }
    
    func load(lessonId: Int) -> [String: Int] {
        UserDefaults.standard.dictionary(forKey: key(for: lessonId)) as? [String: Int] ?? [:]
    }
    
    func grade(lessonId: Int, studentId: Int) -> Int? {
        load(lessonId: lessonId)[String(studentId)]
    }
    
    func clear(lessonId: Int) {
        UserDefaults.standard.removeObject(forKey: key(for: lessonId))
    }
    
    func allPendingLessonIds() -> [Int] {
        let allKeys = UserDefaults.standard.dictionaryRepresentation().keys
        return allKeys
            .filter { $0.hasPrefix("pendingGrades_") }
            .compactMap { Int($0.replacingOccurrences(of: "pendingGrades_", with: "")) }
    }
}
