import SwiftUI

struct AdministracionThirdScreen2: View {
    
    @Binding var selectedPage: Int
    @StateObject private var viewModel = AdminViewModel()
    
    private let rowHeight: CGFloat = 64
    private let spacing: CGFloat = 16
    private let maxVisible: Int = 6
    
    private var listHeight: CGFloat {
        CGFloat(maxVisible) * rowHeight + CGFloat(maxVisible - 1) * spacing
    }
    
    private var groupName: String {
        UserDefaults.standard.string(forKey: "adminJournalGroup") ?? ""
    }
    
    var body: some View {
        
        VStack(spacing: 5) {
            
            Spacer(minLength: 0)
            
            VStack(spacing: 20) {
                
                Text("Группа \(groupName), Выберите предмет:")
                    .font(.system(size: 15))
                    .multilineTextAlignment(.center)
                
                ScrollView(showsIndicators: viewModel.groupSubjects.count > maxVisible) {
                    VStack(spacing: spacing) {
                        ForEach(viewModel.groupSubjects, id: \.self) { subject in
                            Button {
                                UserDefaults.standard.set(subject, forKey: "adminJournalSubject")
                                selectedPage = 4
                            } label: {
                                Text(subject)
                                    .font(.system(size: 15, weight: .medium))
                                    .foregroundColor(.black)
                                    .frame(maxWidth: .infinity)
                                    .frame(height: rowHeight)
                                    .background(glassBg)
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    .padding(.vertical, 4)
                }
                .frame(height: listHeight)
            }
            .padding(20)
            .background(glassBg)
            .padding(.horizontal, 20)
            
            ImageButtonA(image: "back_button") {
                selectedPage = 2
            }
        }
        .padding(.top, 40)
        .onAppear {
            viewModel.loadGroupSubjects(groupName: groupName)
        }
    }
    
    private var glassBg: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 25)
                .fill(.ultraThinMaterial)
                .opacity(0.35)
            RoundedRectangle(cornerRadius: 25)
                .fill(Color.white.opacity(0.4))
            RoundedRectangle(cornerRadius: 25)
                .stroke(Color.white.opacity(0.4), lineWidth: 1)
        }
    }
}

struct ImageButtonA: View {
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
    AdministracionThirdScreen2(selectedPage: .constant(0))
}
