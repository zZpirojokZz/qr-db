import SwiftUI

struct StudentThirdScreen: View {
    
    @StateObject private var viewModel = StudentViewModel()
    
    @State private var startIndex: Int = 0
    @State private var dayOffset: Int = 0
    @State private var showDatePicker: Bool = false
    @State private var pickedDate: Date = Date()
    
    private let visibleRows: Int = 7
    private let visibleDays: Int = 6
    
    private let months = ["Янв", "Фев", "Мар", "Апр", "Май", "Июн",
                          "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"]
    
    private var groupName: String {
        viewModel.profile?.student.group_name
        ?? UserDefaults.standard.string(forKey: "groupName")
        ?? ""
    }
    
    private var baseDate: Date {
        Calendar.current.date(byAdding: .day, value: dayOffset, to: Date()) ?? Date()
    }
    
    private var days: [Date] {
        (0..<visibleDays).compactMap {
            Calendar.current.date(byAdding: .day, value: $0, to: baseDate)
        }
    }
    
    private var subjects: [String] {
        let unique = Set(viewModel.weeklyGrades.map { $0.subject })
        return Array(unique).sorted()
    }
    
    var body: some View {
        
        VStack(spacing: 18) {
            
            Text(groupName)
                .font(.title3)
                .fontWeight(.black)
                .padding(.top, 125)
            
            VStack(spacing: 0) {
                
                HStack(spacing: 0) {
                    TableCellS(text: "Предметы", isHeader: true, width: 100)
                    
                    ForEach(days, id: \.self) { day in
                        TableCellS(text: dayLabel(day), isHeader: true)
                    }
                }
                
                ForEach(0..<visibleRows, id: \.self) { rowIndex in
                    let subject = subjectAt(rowIndex)
                    
                    HStack(spacing: 0) {
                        TableCellS(text: subject ?? "", width: 100)
                        
                        ForEach(days, id: \.self) { day in
                            TableCellS(text: gradeText(for: subject, on: day))
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
                ImageButtonS(image: "up_button", width: 40) {
                    if startIndex > 0 { startIndex -= 1 }
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                
                ImageButtonS(image: "down_button", width: 40) {
                    if startIndex < max(subjects.count - visibleRows, 0) {
                        startIndex += 1
                    }
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
            }
            
            HStack(spacing: 25) {
                ImageButtonS(image: "left_button") {
                    dayOffset -= visibleDays
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                
                ImageButtonS(image: "search_button", width: 106, height: 40) {
                    pickedDate = baseDate
                    showDatePicker = true
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                
                ImageButtonS(image: "right_button") {
                    dayOffset += visibleDays
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
            }
        }
        .padding(.horizontal, 16)
        .onAppear {
            viewModel.loadProfileIfNeeded()
            reloadGrades()
        }
        .onChange(of: dayOffset) {
            reloadGrades()
        }
        .sheet(isPresented: $showDatePicker) {
            datePickerSheet
        }
    }
    
    private var datePickerSheet: some View {
        VStack(spacing: 20) {
            Text("Выберите дату")
                .font(.title3)
                .fontWeight(.semibold)
                .padding(.top, 20)
            
            DatePicker(
                "",
                selection: $pickedDate,
                displayedComponents: [.date]
            )
            .datePickerStyle(.graphical)
            .padding(.horizontal)
            
            HStack(spacing: 16) {
                Button("Отмена") {
                    showDatePicker = false
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.gray.opacity(0.2))
                .cornerRadius(12)
                
                Button("ОК") {
                    applyPickedDate()
                    showDatePicker = false
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue.opacity(0.2))
                .cornerRadius(12)
            }
            .padding(.horizontal)
            
            Spacer()
        }
        .presentationDetents([.medium, .large])
    }
    
    private func applyPickedDate() {
        let cal = Calendar.current
        let today = cal.startOfDay(for: Date())
        let picked = cal.startOfDay(for: pickedDate)
        let diff = cal.dateComponents([.day], from: today, to: picked).day ?? 0
        dayOffset = diff
    }
    
    private func reloadGrades() {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let dateStr = formatter.string(from: baseDate)
        viewModel.loadWeeklyGrades(startDate: dateStr)
    }
    
    private func subjectAt(_ index: Int) -> String? {
        let realIndex = startIndex + index
        guard realIndex < subjects.count else { return nil }
        return subjects[realIndex]
    }
    
    private func dayLabel(_ date: Date) -> String {
        let cal = Calendar.current
        let day = cal.component(.day, from: date)
        let month = months[cal.component(.month, from: date) - 1]
        return "\(day)\n\(month)."
    }
    
    private func gradeText(for subject: String?, on day: Date) -> String {
        guard let subject else { return "" }
        
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let dayStr = formatter.string(from: day)
        
        let item = viewModel.weeklyGrades.first {
            $0.subject == subject &&
            $0.lesson_date.prefix(10) == dayStr
        }
        
        if let grade = item?.grade {
            return "\(grade)"
        }
        return ""
    }
}

struct ImageButtonS: View {
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

struct TableCellS: View {
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
            .overlay(
                Rectangle()
                    .stroke(Color.black, lineWidth: 2)
            )
    }
}

#Preview {
    StudentThirdScreen()
}
