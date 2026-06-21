import SwiftUI

struct AdministracionThirdScreen1: View {
    
    @Binding var selectedPage: Int
    @State private var groupName: String = ""
    @StateObject private var viewModel = AdminViewModel()
    
    var body: some View {
        
        VStack(spacing: 25) {
            
            Spacer()
            
            VStack(spacing: 20) {
                
                Text("Введите название\nгруппы для журнала:")
                    .font(.title3)
                    .multilineTextAlignment(.center)
                
                TextField("Группа", text: $groupName)
                    .textFieldStyle(.plain)
                    .multilineTextAlignment(.center)
                    .autocorrectionDisabled()
                    .onSubmit { goToSubjects() }
                    .padding()
                    .background(glassBg)
            }
            .padding(20)
            .background(glassBg)
            .padding(.horizontal, 24)
            .padding(.top, 190)
            
            ZStack {
                if let lesson = viewModel.currentLesson {
                    Button {
                        UserDefaults.standard.set(lesson.group_name ?? "", forKey: "adminActiveGroup")
                        UserDefaults.standard.set(lesson.subject ?? "", forKey: "adminActiveSubject")
                        UserDefaults.standard.set(lesson.lesson_id, forKey: "adminActiveLessonId")
                        UserDefaults.standard.set(lesson.end_time ?? "", forKey: "adminActiveLessonEnd")
                        selectedPage = 5
                    } label: {
                        Text("Перейти к группе\n\(lesson.group_name ?? "")")
                            .font(.system(size: 18, weight: .medium))
                            .foregroundColor(.black)
                            .multilineTextAlignment(.center)
                            .padding()
                            .frame(maxWidth: 260)
                            .background(glassBg)
                    }
                }
            }
            .frame(maxWidth: 260, minHeight: 100)
            .padding(.top, 170)
            
            Spacer()
        }
        .onAppear {
            viewModel.loadCurrentLesson()
        }
        .onTapGesture { hideKeyboard() }
        .toolbar {
            ToolbarItemGroup(placement: .keyboard) {
                Spacer()
                Button("Готово") { goToSubjects() }
            }
        }
    }
    
    private func goToSubjects() {
        guard !groupName.isEmpty else { return }
        UserDefaults.standard.set(groupName, forKey: "adminJournalGroup")
        selectedPage = 3
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
    
    private func hideKeyboard() {
        UIApplication.shared.sendAction(
            #selector(UIResponder.resignFirstResponder),
            to: nil, from: nil, for: nil
        )
    }
}

#Preview {
    AdministracionThirdScreen1(selectedPage: .constant(0))
}
