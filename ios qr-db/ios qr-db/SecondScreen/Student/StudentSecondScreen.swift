import SwiftUI

struct StudentSecondScreen: View {
    
    @StateObject private var viewModel = StudentViewModel()
    
    private let rowHeight: CGFloat = 60
    private let rowSpacing: CGFloat = 17
    private let maxVisible: Int = 5
    
    private var listHeight: CGFloat {
        CGFloat(maxVisible) * rowHeight
        + CGFloat(maxVisible - 1) * rowSpacing
    }
    
    private var groupName: String {
        viewModel.profile?.student.group_name
        ?? UserDefaults.standard.string(forKey: "groupName")
        ?? ""
    }
    
    var body: some View {
        
        VStack(spacing: 25) {
            
            VStack(spacing: 4) {
                Text(currentDateString())
                    .font(.title)
                    .fontWeight(.black)
                
                Text(groupName)
                    .font(.title3)
                    .fontWeight(.heavy)
            }
            
            Group {
                if viewModel.todaySchedule.isEmpty {
                    
                    Text("На сегодня нет занятий")
                        .font(.headline)
                        .fontWeight(.bold)
                        .foregroundColor(.black)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 40)
                        .padding(.vertical, 20)
                        .frame(maxWidth: .infinity)
                        .background(
                            ZStack {
                                RoundedRectangle(cornerRadius: 15)
                                    .fill(.ultraThinMaterial)
                                    .opacity(0.45)
                                
                                RoundedRectangle(cornerRadius: 15)
                                    .fill(Color.white.opacity(0.7))
                                
                                RoundedRectangle(cornerRadius: 15)
                                    .stroke(Color.black, lineWidth: 2)
                            }
                        )
                    
                } else if viewModel.todaySchedule.count <= maxVisible {
                    
                    VStack(spacing: rowSpacing) {
                        ForEach(viewModel.todaySchedule) { lesson in
                            LessonRowS(lesson: lesson)
                        }
                    }
                    
                } else {
                    
                    ScrollView(showsIndicators: true) {
                        VStack(spacing: rowSpacing) {
                            ForEach(viewModel.todaySchedule) { lesson in
                                LessonRowS(lesson: lesson)
                            }
                        }
                        .padding(.vertical, 2)
                    }
                    .frame(height: listHeight)
                }
            }
            
            Button {
            } label: {
                Text("Скачать расписание")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.blue)
                    .frame(maxWidth: 210)
                    .padding(.vertical, 10)
                    .background(
                        ZStack {
                            RoundedRectangle(cornerRadius: 13)
                                .fill(.ultraThinMaterial)
                                .opacity(0.45)
                            
                            RoundedRectangle(cornerRadius: 13)
                                .fill(Color.white.opacity(0.9))
                            
                            RoundedRectangle(cornerRadius: 13)
                                .stroke(Color.black, lineWidth: 2)
                        }
                    )
            }
        }
        .padding(.horizontal, 24)
        .onAppear {
            viewModel.loadProfileIfNeeded()
            viewModel.loadTodaySchedule()
        }
    }
    
    private func currentDateString() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd.MM.yyyy"
        formatter.locale = Locale(identifier: "ru_RU")
        return formatter.string(from: Date())
    }
}

struct LessonRowS: View {
    
    let lesson: StudentScheduleItem
    
    private var title: String {
        let subject = lesson.subject ?? "Предмет"
        if let teacher = lesson.teacher_name, !teacher.isEmpty {
            return "\(subject), \(teacher)"
        }
        return subject
    }
    
    private var room: String {
        lesson.room ?? "---"
    }
    
    var body: some View {
        
        HStack(spacing: 0) {
            
            ScrollView(.horizontal, showsIndicators: false) {
                Text(title)
                    .font(.system(size: 16))
                    .foregroundColor(.black)
                    .lineLimit(1)
                    .padding(.horizontal, 16)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            Rectangle()
                .fill(Color.black)
                .frame(width: 1)
            
            Text(room)
                .font(.system(size: 17, weight: .medium))
                .frame(width: 70)
        }
        .frame(height: 60)
        .background(
            ZStack {
                RoundedRectangle(cornerRadius: 15)
                    .fill(.ultraThinMaterial)
                    .opacity(0.45)
                
                RoundedRectangle(cornerRadius: 15)
                    .fill(Color.white.opacity(0.7))
                
                RoundedRectangle(cornerRadius: 15)
                    .stroke(Color.black, lineWidth: 2)
            }
        )
        .clipShape(RoundedRectangle(cornerRadius: 15))
    }
}

#Preview {
    StudentSecondScreen()
}
