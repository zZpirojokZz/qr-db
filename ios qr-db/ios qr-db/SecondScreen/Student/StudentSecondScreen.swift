import SwiftUI

struct StudentSecondScreen: View {
    
    let lessons = [
        ("Предмет, преподаватель", "104"),
        ("Предмет, преподаватель", "303"),
        ("Предмет, преподаватель", "400"),
        ("Предмет, преподаватель", "123")
    ]
    
    var body: some View {
        
        VStack(spacing: 25) {
            
            VStack(spacing: 4) {
                Text("дд.мм.гггг")
                    .font(.title2)
                    .fontWeight(.medium)
                
                Text("ИС22-4Б")
                    .font(.subheadline)
                    .foregroundColor(.gray)
            }
            .padding(.horizontal, 30)
            .padding(.vertical, 10)
            .background(
                ZStack {
                    RoundedRectangle(cornerRadius: 25)
                        .fill(.ultraThinMaterial)
                        .opacity(0.45)
                    
                    RoundedRectangle(cornerRadius: 25)
                        .fill(Color.white.opacity(0.7))
                    
                    RoundedRectangle(cornerRadius: 25)
                        .stroke(Color.black, lineWidth: 2)
                }
            )
            
            VStack(spacing: 17) {
                ForEach(lessons.indices, id: \.self) { index in
                    LessonRow(
                        title: lessons[index].0,
                        room: lessons[index].1
                    )
                }
            }
            
            // Кнопка
            Button {
                
            } label: {
                Text("Скачать расписание")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.blue)
                    .frame(maxWidth: 260)
                    .padding(.vertical, 14)
                    .background(
                        ZStack {
                            RoundedRectangle(cornerRadius: 25)
                                .fill(.ultraThinMaterial)
                                .opacity(0.45)
                            
                            RoundedRectangle(cornerRadius: 25)
                                .fill(Color.white.opacity(0.9))
                            
                            RoundedRectangle(cornerRadius: 25)
                                .stroke(Color.black, lineWidth: 2)
                        }
                    )
            }
        }
        .padding(.horizontal, 24)
    }
}

struct LessonRow: View {
    
    let title: String
    let room: String
    
    var body: some View {
        
        HStack(spacing: 0) {
            
            Text(title)
                .font(.system(size: 16))
                .foregroundColor(.black)
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
            
            Divider()
            
            Text(room)
                .font(.system(size: 17, weight: .medium))
                .frame(width: 70)
        }
        .frame(height: 60)
        .background(
            ZStack {
                RoundedRectangle(cornerRadius: 25)
                    .fill(.ultraThinMaterial)
                    .opacity(0.45)
                
                RoundedRectangle(cornerRadius: 25)
                    .fill(Color.white.opacity(0.7))
                
                RoundedRectangle(cornerRadius: 25)
                    .stroke(Color.black, lineWidth: 2)
            }
        )
    }
}

#Preview {
    StudentSecondScreen()
}
