import SwiftUI
//Третий экран, поменять данные на основе фигмы
struct EducatorThirdScreen3: View {
    
    let days = ["9\nФев.", "10\nФев.", "11\nФев.", "12\nФев.", "13\nФев.", "14\nФев."]
    //После БД изменить
    let subjects = (1...10).map { "\($0)" }
    
    @State private var startIndex: Int = 0
    
    var visibleSubjects: [String] {
        Array(subjects.dropFirst(startIndex).prefix(7))
    }
    
    var body: some View {
        
        VStack(spacing: 18) {
            //После БД изменить
            Text("ИС22-4Б")
                .font(.title3)
                .fontWeight(.black)
                .padding(.top, 125)
            VStack(spacing: 0) {
                
                HStack(spacing: 0) {
                    TableCellE(text: "Предметы", isHeader: true, width: 100)
                    
                    ForEach(days, id: \.self) { day in
                        TableCellE(text: day, isHeader: true)
                    }
                    .multilineTextAlignment(.center)
                }
                
                ForEach(visibleSubjects, id: \.self) { subject in
                    HStack(spacing: 0) {
                        TableCellE(text: subject, width: 100)
                    
                        //Пустое место в таблице. После БД изменить
                        ForEach(days, id: \.self) { _ in
                            TableCellE(text: "")
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
                
                ImageButtonE(image: "up_button", width: 40) {
                    if startIndex > 0 {
                        startIndex -= 1
                    }
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                
                ImageButtonE(image: "down_button", width: 40) {
                    if startIndex < subjects.count - 7 {
                        startIndex += 1
                    }
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
            }

            HStack(spacing: 25) {
                
                ImageButtonE(image: "left_button") {
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                
                ImageButtonE(image: "search_button", width: 106, height: 40) {
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                
                ImageButtonE(image: "right_button") {
                }
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
            }
        }
        .padding(.horizontal, 16)
    }
}

import SwiftUI

struct ImageButtonE: View {
    
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

struct TableCellE: View {
    
    let text: String
    var isHeader: Bool = false
    var width: CGFloat? = nil
    
    var body: some View {
        Text(text)
            .font(isHeader ? .system(size: 13, weight: .medium) : .system(size: 13))
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
    EducatorThirdScreen3()
}
