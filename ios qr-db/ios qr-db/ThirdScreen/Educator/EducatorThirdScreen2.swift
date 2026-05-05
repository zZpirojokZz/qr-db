import SwiftUI

struct EducatorThirdScreen2: View {
    
    @Binding var selectedPage: Int
    
    let subjects: [String] = [
        "Физика",
        "Химия",
        "Математика",
        "НВП",
        "Английский",
        "Казахский",
        "История",
        "Информатика"
    ]
    
    private let rowHeight: CGFloat = 64
    private let spacing: CGFloat = 16
    private let maxVisible: Int = 6
    
    var body: some View {
        
        VStack(spacing: 18) {
            
            Spacer(minLength: 10)
            
            VStack(spacing: 20) {
                
                Text("Группа ИС22-4Б,\nВыберите предмет:")
                    .font(.title3)
                    .multilineTextAlignment(.center)
                
                Group {
                    if subjects.count <= maxVisible {
                        // БЕЗ СКРОЛЛА
                        VStack(spacing: spacing) {
                            subjectList
                        }
                    } else {
                        // СО СКРОЛЛОМ
                        ScrollView {
                            VStack(spacing: spacing) {
                                subjectList
                            }
                            .padding(.vertical, 4)
                        }
                        .frame(
                            height: CGFloat(maxVisible) * rowHeight +
                                    CGFloat(maxVisible - 1) * spacing
                        )
                    }
                }
            }
            .padding(20)
            .background(
                ZStack {
                    RoundedRectangle(cornerRadius: 25)
                        .fill(.ultraThinMaterial)
                        .opacity(0.35)
                    
                    RoundedRectangle(cornerRadius: 25)
                        .fill(Color.white.opacity(0.4))
                    
                    RoundedRectangle(cornerRadius: 25)
                        .stroke(Color.white.opacity(0.4), lineWidth: 1)
                }
            )
            .padding(.horizontal, 20)
            
            ImageButton(image: "back_button") {
                selectedPage = 3
            }
        }
    }
    
    private var subjectList: some View {
        ForEach(subjects, id: \.self) { subject in
            
            Button {
                selectedPage = 5
            } label: {
                Text(subject)
                    .font(.system(size: 20, weight: .medium))
                    .foregroundColor(.black)
                    .frame(maxWidth: .infinity)
                    .frame(height: rowHeight)
                    .background(
                        ZStack {
                            RoundedRectangle(cornerRadius: 25)
                                .fill(.ultraThinMaterial)
                                .opacity(0.35)
                            
                            RoundedRectangle(cornerRadius: 25)
                                .fill(Color.white.opacity(0.4))
                            
                            RoundedRectangle(cornerRadius: 25)
                                .stroke(Color.white.opacity(0.4), lineWidth: 1)
                        }
                    )
            }
            .buttonStyle(.plain)
        }
    }
}

struct ImageButton: View {
    
    let image: String
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
                .frame(width: 85, height: 95)
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

#Preview {
    EducatorThirdScreen2(selectedPage: .constant(0))
}
