import SwiftUI

struct StudentSecondScreen: View {
    
    //После БД изменить
    let lessons = [
        ("Предмет, преподаватель", "104"),
        ("Предмет, преподаватель", "303"),
        ("Предмет, преподаватель", "400"),
        ("Предмет, преподаватель", "123")
    ]
    
    var body: some View {
        
        VStack(spacing: 25) {
            //После БД изменить
            VStack(spacing: 4) {
                Text("дд.мм.гггг")
                    .font(.title)
                    .fontWeight(.black)
                //После БД изменить
                Text("ИС22-4Б")
                    .font(.title3)
                    .fontWeight(.heavy)
            }
            
            VStack(spacing: 17) {
                ForEach(lessons.indices, id: \.self) { index in
                    LessonRowS(
                        title: lessons[index].0,
                        room: lessons[index].1
                    )
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
    }
}

struct LessonRowS: View {
    
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
                RoundedRectangle(cornerRadius: 15)
                    .fill(.ultraThinMaterial)
                    .opacity(0.45)
                
                RoundedRectangle(cornerRadius: 15)
                    .fill(Color.white.opacity(0.7))
                
                RoundedRectangle(cornerRadius: 15)
                    .stroke(Color.black, lineWidth: 2)
            }
        )
    }
}

#Preview {
    StudentSecondScreen()
}
