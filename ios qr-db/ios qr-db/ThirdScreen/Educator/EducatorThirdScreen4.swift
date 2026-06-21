import SwiftUI

struct EducatorThirdScreen4: View {
    
    @Binding var selectedPage: Int
    @StateObject private var viewModel = TeacherViewModel()
    
    @State private var startIndex: Int = 0
    @State private var editingStudent: GroupStudent? = nil
    @State private var inputGrade: String = ""
    
    @State private var localGrades: [Int: Int] = [:]
    
    @State private var endTimer: Timer? = nil
    
    private let visibleRows: Int = 7
    
    private var groupName: String {
        UserDefaults.standard.string(forKey: "activeGroup") ?? ""
    }
    private var lessonId: Int {
        UserDefaults.standard.integer(forKey: "activeLessonId")
    }
    private var lessonEndTime: Date? {
        guard let str = UserDefaults.standard.string(forKey: "activeLessonEnd") else {
            return nil
        }
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return formatter.date(from: str)
    }
    
    private var todayString: String {
        let f = DateFormatter()
        f.dateFormat = "dd.MM.yyyy"
        return f.string(from: Date())
    }
    
    var body: some View {
        
        ZStack(alignment: .topLeading) {
            
            VStack(spacing: 18) {
                
                HStack(spacing: 12) {
                    ImageButtonE(image: "Arrow", width: 25, height: 25) {
                        selectedPage = 2
                    }
                    
                    Text(groupName)
                        .font(.title3)
                        .fontWeight(.black)
                    
                    Spacer()
                }
                .padding(.top, 60)
                
                VStack(spacing: 0) {
                    
                    HStack(spacing: 0) {
                        TableCell4(text: "Студент", isHeader: true)
                        TableCell4(text: todayString, isHeader: true)
                    }
                    
                    ForEach(0..<visibleRows, id: \.self) { rowIndex in
                        let student = studentAt(rowIndex)
                        
                        HStack(spacing: 0) {
                            TableCell4(text: student?.full_name ?? "")
                            
                            if let student {
                                Button {
                                    inputGrade = localGrades[student.user_id].map { "\($0)" } ?? ""
                                    editingStudent = student
                                } label: {
                                    gradeCell(for: student)
                                }
                                .buttonStyle(.plain)
                            } else {
                                gradeCellEmpty()
                            }
                        }
                    }
                }
                .background(
                    ZStack {
                        RoundedRectangle(cornerRadius: 22)
                            .fill(.ultraThinMaterial)
                            .opacity(0.5)
                        
                        RoundedRectangle(cornerRadius: 22)
                            .fill(Color.white.opacity(0.4))
                        
                        RoundedRectangle(cornerRadius: 22)
                            .stroke(Color.black, lineWidth: 3)
                    }
                )
                .clipShape(RoundedRectangle(cornerRadius: 22))
                .shadow(color: .black.opacity(0.15), radius: 6, x: 0, y: 4)
                .padding(.top, 55)
                
                HStack(spacing: 30) {
                    ImageButtonE(image: "up_button", width: 55) {
                        if startIndex > 0 { startIndex -= 1 }
                    }
                    ImageButtonE(image: "down_button", width: 55) {
                        if startIndex < max(viewModel.groupStudents.count - visibleRows, 0) {
                            startIndex += 1
                        }
                    }
                }
                .padding(.top, 25)
                
                Spacer()
            }
            .padding(.horizontal, 16)
        }
        .onAppear {
            if !groupName.isEmpty {
                viewModel.loadGroupStudents(groupName: groupName)
            }
            if lessonId > 0 {
                viewModel.loadAttendance(lessonId: lessonId)
                
                let stored = GradeStorage.shared.load(lessonId: lessonId)
                localGrades = stored.reduce(into: [Int: Int]()) { result, pair in
                    if let id = Int(pair.key) {
                        result[id] = pair.value
                    }
                }
            }
            
            scheduleEndTimer()
        }
        .onDisappear {
            endTimer?.invalidate()
            endTimer = nil
        }
        .alert(
            "Оценка",
            isPresented: Binding(
                get: { editingStudent != nil },
                set: { if !$0 { editingStudent = nil } }
            )
        ) {
            TextField("1-100", text: $inputGrade)
                .keyboardType(.numberPad)
            
            Button("Сохранить") {
                saveGradeLocally()
            }
            
            Button("Отмена", role: .cancel) {
                editingStudent = nil
            }
        } message: {
            if let s = editingStudent {
                Text("Студент: \(s.full_name)")
            }
        }
    }
    
    private func saveGradeLocally() {
        guard let student = editingStudent,
              let grade = Int(inputGrade),
              grade >= 1, grade <= 100,
              lessonId > 0
        else {
            editingStudent = nil
            return
        }
        
        GradeStorage.shared.save(
            lessonId: lessonId,
            studentId: student.user_id,
            grade: grade
        )
        
        localGrades[student.user_id] = grade
        
        editingStudent = nil
    }
    
    private func scheduleEndTimer() {
        endTimer?.invalidate()
        endTimer = nil
        
        guard let end = lessonEndTime else { return }
        
        let interval = end.timeIntervalSince(Date())
        guard interval > 0 else {
            flush()
            return
        }
        
        endTimer = Timer.scheduledTimer(
            withTimeInterval: interval + 2,
            repeats: false
        ) { _ in
            flush()
        }
    }
    
    private func flush() {
        guard lessonId > 0 else { return }
        viewModel.flushPendingGrades(lessonId: lessonId) {
            localGrades = [:]
        }
    }
    
    private func studentAt(_ index: Int) -> GroupStudent? {
        let realIndex = startIndex + index
        guard realIndex < viewModel.groupStudents.count else { return nil }
        return viewModel.groupStudents[realIndex]
    }
    
    private func attendanceFor(_ student: GroupStudent) -> LessonAttendance? {
        viewModel.attendance.first { $0.user_id == student.user_id }
    }
    
    @ViewBuilder
    private func gradeCell(for student: GroupStudent) -> some View {
        let localGrade = localGrades[student.user_id]
        let att = attendanceFor(student)
        let attended = att?.attendance ?? false
        
        Group {
            if let grade = localGrade {
                Text("\(grade)")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(.black)
            } else if attended {
                Text("✓")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(.black)
            } else {
                Text("")
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: 44)
        .background(bgColor(for: localGrade, attended: attended))
        .overlay(Rectangle().stroke(Color.black, lineWidth: 2))
    }
    
    private func gradeCellEmpty() -> some View {
        Text("")
            .frame(maxWidth: .infinity)
            .frame(height: 44)
            .background(Color.white.opacity(0.35))
            .overlay(Rectangle().stroke(Color.black, lineWidth: 2))
    }
    
    private func bgColor(for grade: Int?, attended: Bool) -> Color {
        guard let g = grade else {
            return attended
                ? Color.green.opacity(0.3)
                : Color.white.opacity(0.35)
        }
        switch g {
        case 0:        return Color.gray.opacity(0.4)
        case 1...49:   return Color(red: 0.90, green: 0.45, blue: 0.45).opacity(0.6)
        case 50...69:  return Color(red: 1.00, green: 0.83, blue: 0.31).opacity(0.6)
        case 70...89:  return Color(red: 0.68, green: 0.84, blue: 0.50).opacity(0.6)
        case 90...100: return Color(red: 0.40, green: 0.73, blue: 0.41).opacity(0.6)
        default:       return Color.white.opacity(0.35)
        }
    }
}

struct ImageButtonE: View {
    let image: String
    var width: CGFloat = 85
    var height: CGFloat = 85
    var action: () -> Void
    @State private var pressed = false
    
    var body: some View {
        Button {
            UIImpactFeedbackGenerator(style: .light).impactOccurred()
            action()
        } label: {
            Image(image)
                .resizable()
                .scaledToFit()
                .frame(width: width, height: height)
                .scaleEffect(pressed ? 0.92 : 1)
                .opacity(pressed ? 0.8 : 1)
        }
        .buttonStyle(.plain)
        .simultaneousGesture(
            DragGesture(minimumDistance: 0)
                .onChanged { _ in pressed = true }
                .onEnded { _ in pressed = false }
        )
    }
}

struct TableCell4: View {
    let text: String
    var isHeader: Bool = false
    
    var body: some View {
        Text(text)
            .font(isHeader ? .system(size: 14, weight: .medium) : .system(size: 14))
            .multilineTextAlignment(.center)
            .frame(maxWidth: .infinity)
            .frame(height: 44)
            .background(Color.white.opacity(0.35))
            .overlay(Rectangle().stroke(Color.black, lineWidth: 2))
    }
}

#Preview {
    EducatorThirdScreen4(selectedPage: .constant(0))
}
