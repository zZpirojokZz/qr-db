import SwiftUI

struct AdministracionThirdScreen3: View {
    
    @Binding var selectedPage: Int
    @StateObject private var viewModel = AdminViewModel()
    
    @State private var startIndex: Int = 0
    @State private var dayOffset: Int = 0
    @State private var showDatePicker: Bool = false
    @State private var pickedDate: Date = Date()
    
    // Редактирование любой клетки
    @State private var editingCell: (student: GroupStudent, day: Date)? = nil
    @State private var inputGrade: String = ""
    
    private let visibleRows: Int = 7
    private let visibleDays: Int = 6
    
    private var groupName: String {
        UserDefaults.standard.string(forKey: "adminJournalGroup") ?? ""
    }
    private var subject: String {
        UserDefaults.standard.string(forKey: "adminJournalSubject") ?? ""
    }
    
    private let months = ["Янв", "Фев", "Мар", "Апр", "Май", "Июн",
                          "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"]
    
    private var baseDate: Date {
        Calendar.current.date(byAdding: .day, value: dayOffset, to: Date()) ?? Date()
    }
    
    private var days: [Date] {
        (0..<visibleDays).compactMap {
            Calendar.current.date(byAdding: .day, value: $0, to: baseDate)
        }
    }
    
    var body: some View {
        
        ZStack(alignment: .topLeading) {
            
            VStack(spacing: 18) {
                
                Text(groupName)
                    .font(.title3)
                    .fontWeight(.black)
                    .padding(.top, 125)
                
                VStack(spacing: 0) {
                    
                    HStack(spacing: 0) {
                        TableCellA3(text: "Предмет", isHeader: true, width: 100)
                        
                        ForEach(days, id: \.self) { day in
                            TableCellA3(text: dayLabel(day), isHeader: true)
                        }
                    }
                    
                    ForEach(0..<visibleRows, id: \.self) { rowIndex in
                        let student = studentAt(rowIndex)
                        
                        HStack(spacing: 0) {
                            TableCellA3(text: student?.full_name ?? "", width: 100)
                            
                            ForEach(days, id: \.self) { day in
                                if let s = student {
                                    let canEdit = gradeItem(for: s, on: day)?.lesson_id != nil
                                    
                                    if canEdit {
                                        Button {
                                            let item = gradeItem(for: s, on: day)
                                            inputGrade = item?.grade.map { "\($0)" } ?? ""
                                            editingCell = (s, day)
                                        } label: {
                                            gradeCell(text: gradeText(for: s, on: day), editable: true)
                                        }
                                        .buttonStyle(.plain)
                                    } else {
                                        gradeCell(text: gradeText(for: s, on: day), editable: false)
                                    }
                                } else {
                                    gradeCell(text: "", editable: false)
                                }
                            }
                        }
                    }
                }
                .background(
                    ZStack {
                        RoundedRectangle(cornerRadius: 22)
                            .fill(.ultraThinMaterial)
                            .opacity(0.45)
                        RoundedRectangle(cornerRadius: 22)
                            .fill(Color.white.opacity(0.5))
                        RoundedRectangle(cornerRadius: 22)
                            .stroke(Color.black, lineWidth: 4)
                    }
                )
                .clipShape(RoundedRectangle(cornerRadius: 22))
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                
                HStack(spacing: 30) {
                    ImageButtonA3(image: "up_button", width: 40) {
                        if startIndex > 0 { startIndex -= 1 }
                    }
                    .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                    
                    ImageButtonA3(image: "down_button", width: 40) {
                        if startIndex < max(viewModel.groupStudents.count - visibleRows, 0) {
                            startIndex += 1
                        }
                    }
                    .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                }
                
                HStack(spacing: 25) {
                    ImageButtonA3(image: "left_button") {
                        dayOffset -= visibleDays
                    }
                    .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                    
                    ImageButtonA3(image: "search_button", width: 106, height: 40) {
                        pickedDate = baseDate
                        showDatePicker = true
                    }
                    .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                    
                    ImageButtonA3(image: "right_button") {
                        dayOffset += visibleDays
                    }
                    .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                }
            }
            .padding(.horizontal, 16)
            
            ImageButtonA3(image: "Arrow", width: 25, height: 45) {
                selectedPage = 3
            }
            .padding(.top, 60)
            .padding(.leading, 16)
        }
        .onAppear {
            viewModel.loadGroupStudents(groupName: groupName)
            reloadGrades()
        }
        .onChange(of: dayOffset) {
            reloadGrades()
        }
        .sheet(isPresented: $showDatePicker) {
            datePickerSheet
        }
        .alert(
            "Оценка",
            isPresented: Binding(
                get: { editingCell != nil },
                set: { if !$0 { editingCell = nil } }
            )
        ) {
            TextField("1-100", text: $inputGrade)
                .keyboardType(.numberPad)
            
            Button("Сохранить") {
                saveGrade()
            }
            
            Button("Отмена", role: .cancel) {
                editingCell = nil
            }
        } message: {
            if let cell = editingCell {
                Text("Студент: \(cell.student.full_name)\nДата: \(dateOnly(cell.day))")
            }
        }
    }
    
    private func saveGrade() {
        guard let cell = editingCell,
              let grade = Int(inputGrade),
              grade >= 1, grade <= 100
        else {
            editingCell = nil
            return
        }
        
        let item = gradeItem(for: cell.student, on: cell.day)
        guard let lessonId = item?.lesson_id else {
            editingCell = nil
            return
        }
        
        let f = DateFormatter()
        f.dateFormat = "yyyy-MM-dd"
        let dayStr = f.string(from: cell.day)
        
        viewModel.updateLocalWeeklyGrade(
            studentId: cell.student.user_id,
            lessonDate: dayStr,
            grade: grade
        )
        
        viewModel.setGrade(
            lessonId: lessonId,
            studentId: cell.student.user_id,
            grade: grade
        ) {
            
        }
        
        editingCell = nil
    }
    
    private var datePickerSheet: some View {
        VStack(spacing: 20) {
            Text("Выберите дату").font(.title3).fontWeight(.semibold).padding(.top, 20)
            
            DatePicker("", selection: $pickedDate, displayedComponents: [.date])
                .datePickerStyle(.graphical)
                .padding(.horizontal)
            
            HStack(spacing: 16) {
                Button("Отмена") { showDatePicker = false }
                    .frame(maxWidth: .infinity).padding()
                    .background(Color.gray.opacity(0.2)).cornerRadius(12)
                
                Button("ОК") {
                    let cal = Calendar.current
                    let today = cal.startOfDay(for: Date())
                    let picked = cal.startOfDay(for: pickedDate)
                    let diff = cal.dateComponents([.day], from: today, to: picked).day ?? 0
                    dayOffset = diff
                    showDatePicker = false
                }
                .frame(maxWidth: .infinity).padding()
                .background(Color.blue.opacity(0.2)).cornerRadius(12)
            }
            .padding(.horizontal)
            
            Spacer()
        }
        .presentationDetents([.medium, .large])
    }
    
    private func studentAt(_ index: Int) -> GroupStudent? {
        let real = startIndex + index
        guard real < viewModel.groupStudents.count else { return nil }
        return viewModel.groupStudents[real]
    }
    
    private func reloadGrades() {
        let f = DateFormatter()
        f.dateFormat = "yyyy-MM-dd"
        viewModel.loadWeeklyGrades(
            groupName: groupName,
            subject: subject,
            startDate: f.string(from: baseDate)
        )
    }
    
    private func dayLabel(_ date: Date) -> String {
        let cal = Calendar.current
        let day = cal.component(.day, from: date)
        let month = months[cal.component(.month, from: date) - 1]
        return "\(day)\n\(month)."
    }
    
    private func dateOnly(_ date: Date) -> String {
        let f = DateFormatter()
        f.dateFormat = "dd.MM.yyyy"
        return f.string(from: date)
    }
    
    private func gradeItem(for student: GroupStudent, on day: Date) -> WeeklyGradeItem? {
        let f = DateFormatter()
        f.dateFormat = "yyyy-MM-dd"
        let dayStr = f.string(from: day)
        
        return viewModel.weeklyGrades.first {
            $0.student_id == student.user_id &&
            $0.lesson_date.prefix(10) == dayStr
        }
    }
    
    private func gradeText(for student: GroupStudent, on day: Date) -> String {
        if let g = gradeItem(for: student, on: day)?.grade {
            return "\(g)"
        }
        return ""
    }
    
    private func gradeCell(text: String, editable: Bool) -> some View {
        Text(text)
            .font(.system(size: 13))
            .multilineTextAlignment(.center)
            .frame(height: 44)
            .frame(maxWidth: .infinity)
            .background(editable ? Color.white.opacity(0.35) : Color.gray.opacity(0.5))
            .overlay(Rectangle().stroke(Color.black, lineWidth: 2))
    }
    
    struct ImageButtonA3: View {
        let image: String
        var width: CGFloat = 70
        var height: CGFloat = 70
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
    
    struct TableCellA3: View {
        let text: String
        var isHeader: Bool = false
        var width: CGFloat? = nil
        
        var body: some View {
            Text(text)
                .font(isHeader ? .system(size: 13, weight: .medium) : .system(size: 13))
                .multilineTextAlignment(.center)
                .frame(width: width, height: 44)
                .frame(maxWidth: width == nil ? .infinity : nil)
                .background(Color.white.opacity(0.35))
                .overlay(Rectangle().stroke(Color.black, lineWidth: 2))
        }
    }
}

#Preview {
    AdministracionThirdScreen3(selectedPage: .constant(0))
}
