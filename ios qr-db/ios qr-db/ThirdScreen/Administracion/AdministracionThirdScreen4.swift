import SwiftUI

struct AdministracionThirdScreen4: View {
    
    @Binding var selectedPage: Int
    //После БД изменить
    let students = (1...12).map { "Фамилия Имя \($0)" }
    @State private var startIndex: Int = 0
    @State private var values: [String: String] = [:]
    
    var visibleStudents: [String] {
        Array(students.dropFirst(startIndex).prefix(7))
    }
    
    var body: some View {
        
        ZStack(alignment: .topLeading) {
            
            VStack(spacing: 18) {
                
                HStack(spacing: 12) {
                    
                    ImageButtonA1(image: "Arrow", width: 25, height: 25) {
                        selectedPage = 2
                    }
                    //После БД изменить
                    Text("ИС22-4Б")
                        .font(.title3)
                        .fontWeight(.black)
                        .multilineTextAlignment(.center)
                    
                    Spacer()
                }
                .padding(.top, 60)
                
                VStack(spacing: 0) {
                    
                    HStack(spacing: 0) {
                        TableCellA(text: "Предмет", isHeader: true) //После БД изменить
                        TableCellA(text: "04.02.2026", isHeader: true) //После БД изменить
                    }
                    
                    ForEach(visibleStudents, id: \.self) { student in
                        HStack(spacing: 0) {
                            
                            TableCellA(text: student)
                            
                            TextField(
                                "",
                                text: Binding(
                                    get: {
                                        values[student] ?? ""
                                    },
                                    set: { newValue in
                                        values[student] = newValue
                                    }
                                )
                            )
                            .multilineTextAlignment(.center)
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .background(Color.white.opacity(0.35))
                            .overlay(
                                Rectangle()
                                    .stroke(Color.black, lineWidth: 2)
                            )
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
                    
                    ImageButtonA1(image: "up_button", width: 55) {
                        if startIndex > 0 {
                            startIndex -= 1
                        }
                    }
                    
                    ImageButtonA1(image: "down_button", width: 55) {
                        if startIndex < students.count - 7 {
                            startIndex += 1
                        }
                    }
                }
                .padding(.top, 25)
                
                Spacer()
            }
            .padding(.horizontal, 16)
        }
    }
}

struct ImageButtonA1: View {
    
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

struct TableCellA: View {
    
    let text: String
    var isHeader: Bool = false
    
    var body: some View {
        Text(text)
            .font(isHeader ? .system(size: 14, weight: .medium) : .system(size: 14))
            .frame(maxWidth: .infinity)
            .frame(height: 44)
            .background(Color.white.opacity(0.35))
            .overlay(
                Rectangle()
                    .stroke(Color.black, lineWidth: 2)
            )
    }
}

#Preview {
    AdministracionThirdScreen4(selectedPage: .constant(0))
}
