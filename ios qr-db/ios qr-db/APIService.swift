import Foundation

struct LoginRequest: Codable {
    let email: String
    let password: String
}

struct LoginResponse: Codable {
    let message: String
    let token: String
    let role_id: Int
    let user_id: Int
    let full_name: String?
    let email: String?
    let group_name: String?
}

struct UserProfile: Codable {
    let user_id: Int
    let full_name: String
    let email: String
    let role_id: Int
    let phone: String?
    let sub_role: String?
    let department: String?
    let group_id: Int?
    let group_name: String?
}

struct MarkAttendanceRequest: Codable {
    let lesson_id: Int
    let student_id: Int
}

struct MarkAttendanceResponse: Codable {
    let message: String
}

struct TeacherProfileResponse: Codable {
    let teacher: TeacherInfo
    let curated_group: String?
    let group_leader: ContactPerson?
    let department_head: ContactPerson?
}

struct TeacherInfo: Codable {
    let user_id: Int
    let full_name: String
    let email: String?
    let role_id: Int?
    let phone: String?
}

struct ContactPerson: Codable {
    let user_id: Int?
    let full_name: String?
    let phone: String?
}

struct TeacherLesson: Codable, Identifiable {
    let lesson_id: Int
    let teacher_id: Int?
    let group_id: Int?
    let subject: String?
    let start_time: String?
    let end_time: String?
    let room: String?
    let group_name: String?
    
    var id: Int { lesson_id }
}

struct LessonAttendance: Codable, Identifiable {
    let user_id: Int
    let full_name: String?
    let attendance: Bool?
    let grade: Int?
    
    var student_id: Int { user_id }
    var id: Int { user_id }
}

struct GroupStudent: Codable, Identifiable {
    let user_id: Int
    let full_name: String
    
    var id: Int { user_id }
}

struct WeeklyGradeItem: Codable {
    let student_id: Int
    let full_name: String?
    let grade: Int?
    let attendance: Bool?
    let lesson_date: String
    let lesson_id: Int?
}

struct SetGradeRequest: Codable {
    let lesson_id: Int
    let student_id: Int
    let grade: Int?
    let attendance: Bool
}

struct GroupActiveLesson: Codable {
    let lesson_id: Int
    let subject: String?
    let room: String?
}

struct StudentProfileResponse: Codable {
    let student: StudentInfo
    let curator: ContactPerson?
    let group_leader: ContactPerson?
    let department_head: ContactPerson?
}

struct StudentInfo: Codable {
    let user_id: Int
    let full_name: String
    let email: String?
    let role_id: Int?
    let phone: String?
    let group_name: String?
}

struct StudentScheduleItem: Codable, Identifiable {
    let lesson_id: Int?
    let subject: String?
    let teacher_name: String?
    let group_name: String?
    let room: String?
    let start_time: String?
    let end_time: String?
    let date: String?
    
    var id: Int { lesson_id ?? UUID().hashValue }
}

struct StudentWeeklyGradeItem: Codable {
    let subject: String
    let grade: Int?
    let attendance: Bool?
    let lesson_date: String
}

class APIService {
    
    static let shared = APIService()
    
    private init() {}
    
    let baseURL = "https://smartcheck.aspc.kz"
    
    func markAttendance(
        qrCode: String,
        studentId: Int,
        completion: @escaping (Result<String, Error>) -> Void
    ) {
        
        let parts = qrCode.components(separatedBy: "_")
        
        guard let lessonId = Int(parts.first ?? "") else {
            completion(.failure(NSError(
                domain: "QR",
                code: -1,
                userInfo: [NSLocalizedDescriptionKey: "Неверный QR-код"]
            )))
            return
        }
        
        guard let url = URL(string: "\(baseURL)/grades/mark") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if let token = UserDefaults.standard.string(forKey: "token") {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        let body: [String: Any] = [
            "lesson_id": lessonId,
            "student_id": studentId,
            "attendance": true
        ]
        
        request.httpBody = try? JSONSerialization.data(withJSONObject: body)
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                
                if let http = response as? HTTPURLResponse,
                   !(200..<300).contains(http.statusCode) {
                    completion(.failure(NSError(
                        domain: "API",
                        code: http.statusCode,
                        userInfo: [NSLocalizedDescriptionKey: "Не удалось отметиться (\(http.statusCode))"]
                    )))
                    return
                }
                
                completion(.success("Вы успешно отметились"))
            }
        }.resume()
    }
    
    func login(
        email: String,
        password: String,
        completion: @escaping (Result<LoginResponse, Error>) -> Void
    ) {
        guard let url = URL(string: "\(baseURL)/auth/login") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body = LoginRequest(email: email, password: password)
        request.httpBody = try? JSONEncoder().encode(body)
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data else { return }
                do {
                    let result = try JSONDecoder().decode(LoginResponse.self, from: data)
                    completion(.success(result))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    struct UserInfo: Codable {
        let user_id: Int
        let full_name: String
        let phone: String?
        let sub_role: String?
    }
    
    func getUserById(
        id: Int,
        completion: @escaping (Result<UserInfo, Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        guard let url = URL(string: "\(baseURL)/users/\(id)") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, _, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data else { return }
                do {
                    let user = try JSONDecoder().decode(UserInfo.self, from: data)
                    completion(.success(user))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func getTeacherProfile(
        teacherId: Int,
        completion: @escaping (Result<TeacherProfileResponse, Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        guard let url = URL(string: "\(baseURL)/teacher/profile/\(teacherId)") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data else { return }
                do {
                    let profile = try JSONDecoder().decode(TeacherProfileResponse.self, from: data)
                    completion(.success(profile))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func getCurrentTeacherLesson(
        teacherId: Int,
        completion: @escaping (Result<TeacherLesson?, Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        guard let url = URL(string: "\(baseURL)/lessons/teacher-today/\(teacherId)") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data, !data.isEmpty else {
                    completion(.success(nil))
                    return
                }
                do {
                    let lessons = try JSONDecoder().decode([TeacherLesson].self, from: data)
                    
                    let formatter = ISO8601DateFormatter()
                    formatter.formatOptions = [
                        .withInternetDateTime,
                        .withFractionalSeconds
                    ]
                    
                    let now = Date()
                    
                    let current = lessons.first { lesson in
                        guard let startStr = lesson.start_time,
                              let endStr = lesson.end_time,
                              let start = formatter.date(from: startStr),
                              let end = formatter.date(from: endStr)
                        else { return false }
                        return now >= start && now <= end
                    }
                    
                    completion(.success(current))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func getTodayTeacherLessons(
        teacherId: Int,
        completion: @escaping (Result<[TeacherLesson], Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        guard let url = URL(string: "\(baseURL)/lessons/teacher-today/\(teacherId)") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data, !data.isEmpty else {
                    completion(.success([]))
                    return
                }
                do {
                    let lessons = try JSONDecoder().decode([TeacherLesson].self, from: data)
                    completion(.success(lessons))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func getSubjectsByGroup(
        groupName: String,
        completion: @escaping (Result<[String], Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        
        let encoded = groupName.addingPercentEncoding(
            withAllowedCharacters: .urlPathAllowed
        ) ?? groupName
        
        guard let url = URL(string: "\(baseURL)/lessons/group-subjects/\(encoded)") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data else { return }
                do {
                    let subjects = try JSONDecoder().decode([String].self, from: data)
                    completion(.success(subjects))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func getGroupStudents(
        groupName: String,
        completion: @escaping (Result<[GroupStudent], Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        
        let encoded = groupName.addingPercentEncoding(
            withAllowedCharacters: .urlPathAllowed
        ) ?? groupName
        
        guard let url = URL(string: "\(baseURL)/lessons/group-students/\(encoded)") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data else { return }
                do {
                    let students = try JSONDecoder().decode([GroupStudent].self, from: data)
                    completion(.success(students))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func getWeeklyGrades(
        groupName: String,
        subject: String,
        startDate: String,
        completion: @escaping (Result<[WeeklyGradeItem], Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        
        var components = URLComponents(string: "\(baseURL)/grades/weekly")
        components?.queryItems = [
            URLQueryItem(name: "groupName", value: groupName),
            URLQueryItem(name: "subject", value: subject),
            URLQueryItem(name: "startDate", value: startDate)
        ]
        
        guard let url = components?.url else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data else { return }
                do {
                    let grades = try JSONDecoder().decode([WeeklyGradeItem].self, from: data)
                    completion(.success(grades))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func getGroupActiveLesson(
        groupName: String,
        completion: @escaping (Result<GroupActiveLesson?, Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        
        let encoded = groupName.addingPercentEncoding(
            withAllowedCharacters: .urlPathAllowed
        ) ?? groupName
        
        guard let url = URL(string: "\(baseURL)/lessons/group-active/\(encoded)") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data, !data.isEmpty else {
                    completion(.success(nil))
                    return
                }
                do {
                    let lesson = try JSONDecoder().decode(GroupActiveLesson.self, from: data)
                    completion(.success(lesson))
                } catch {
                    completion(.success(nil))
                }
            }
        }.resume()
    }
    
    func getLessonAttendance(
        lessonId: Int,
        completion: @escaping (Result<[LessonAttendance], Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        guard let url = URL(string: "\(baseURL)/grades/lesson/\(lessonId)/attendance") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data else { return }
                do {
                    let list = try JSONDecoder().decode([LessonAttendance].self, from: data)
                    completion(.success(list))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func setGrade(
        lessonId: Int,
        studentId: Int,
        grade: Int?,
        attendance: Bool,
        completion: @escaping (Result<Void, Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        guard let url = URL(string: "\(baseURL)/grades/mark") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        let body = SetGradeRequest(
            lesson_id: lessonId,
            student_id: studentId,
            grade: grade,
            attendance: attendance
        )
        
        request.httpBody = try? JSONEncoder().encode(body)
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                completion(.success(()))
            }
        }.resume()
    }
    
    func getStudentProfile(
        studentId: Int,
        completion: @escaping (Result<StudentProfileResponse, Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        guard let url = URL(string: "\(baseURL)/student/profile/\(studentId)") else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data else { return }
                do {
                    let profile = try JSONDecoder().decode(StudentProfileResponse.self, from: data)
                    completion(.success(profile))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
    
    func getStudentSchedule(
        studentId: Int,
        completion: @escaping (Result<[StudentScheduleItem], Error>) -> Void
    ) {
        
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        
        guard let url = URL(
            string: "\(baseURL)/schedule/student/\(studentId)"
        ) else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            
            DispatchQueue.main.async {
                
                if let error = error {
                    completion(.failure(error))
                    return
                }
                
                guard let data = data, !data.isEmpty else {
                    completion(.success([]))
                    return
                }
                
                do {
                    let list = try JSONDecoder().decode(
                        [StudentScheduleItem].self,
                        from: data
                    )
                    completion(.success(list))
                } catch {
                    print("STUDENT SCHEDULE DECODE ERROR:", error)
                    print("RAW:", String(data: data, encoding: .utf8) ?? "nil")
                    completion(.failure(error))
                }
            }
            
        }.resume()
    }
    
    func getStudentWeeklyGrades(
        studentId: Int,
        startDate: String,
        completion: @escaping (Result<[StudentWeeklyGradeItem], Error>) -> Void
    ) {
        guard let token = UserDefaults.standard.string(forKey: "token") else { return }
        
        var components = URLComponents(string: "\(baseURL)/grades/student-weekly")
        components?.queryItems = [
            URLQueryItem(name: "studentId", value: "\(studentId)"),
            URLQueryItem(name: "startDate", value: startDate)
        ]
        
        guard let url = components?.url else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    completion(.failure(error))
                    return
                }
                guard let data = data else { return }
                
                print("📦 STUDENT WEEKLY RAW:", String(data: data, encoding: .utf8) ?? "nil")
                
                do {
                    let list = try JSONDecoder().decode([StudentWeeklyGradeItem].self, from: data)
                    completion(.success(list))
                } catch {
                    completion(.failure(error))
                }
            }
        }.resume()
    }
}
