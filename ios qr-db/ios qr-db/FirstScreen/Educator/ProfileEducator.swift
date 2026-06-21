import SwiftUI

struct ProfileEducator: View {
    
    @Environment(\.dismiss) var dismiss
    
    @AppStorage("isLoggedIn") private var isLoggedIn = false
    @AppStorage("roleID") private var roleID = 0
    
    @ObservedObject var viewModel: TeacherViewModel
    
    var body: some View {
        
        ZStack {
            
            Image("background")
                .resizable()
                .scaledToFill()
                .ignoresSafeArea()
            
            VStack(spacing: 20) {
                
                Spacer()
                
                VStack(spacing: 5) {
                    
                    Image("iconprof")
                        .frame(width: 90, height: 90)
                        .shadow(radius: 0.5)
                        .padding(.vertical, 20)
                    
                    Text(
                        viewModel.teacherProfile?.teacher.full_name
                        ?? UserDefaults.standard.string(forKey: "fullName")
                        ?? "Имя Фамилия"
                    )
                    .font(.title2)
                    .fontWeight(.semibold)
                    .multilineTextAlignment(.center)
                    .padding(.bottom, 10)
                    
                    Divider()
                    
                    Text(
                        viewModel.teacherProfile?.curated_group
                        ?? UserDefaults.standard.string(forKey: "curatedGroup")
                        ?? "Курируемая группа"
                    )
                    .font(.headline)
                    .padding(.bottom, 15)
                    .padding(.top, 10)
                }
                .frame(maxWidth: 320)
                .background(glassBg)
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                .cornerRadius(25)
                
                VStack {
                    
                    VStack {
                        Text(
                            viewModel.teacherProfile?.group_leader?.full_name
                            ?? "ФИО старосты"
                        )
                        .font(.title2)
                        .fontWeight(.semibold)
                        .multilineTextAlignment(.center)
                        
                        Text(
                            viewModel.teacherProfile?.group_leader?.phone
                            ?? "Номер"
                        )
                        .font(.title3)
                    }
                    .padding(.top, 25)
                    .padding(.bottom, 20)
                    
                    Divider()
                    
                    VStack {
                        Text(
                            viewModel.teacherProfile?.department_head?.full_name
                            ?? "ФИО зав. отделения"
                        )
                        .font(.title2)
                        .fontWeight(.semibold)
                        .multilineTextAlignment(.center)
                        
                        Text(
                            viewModel.teacherProfile?.department_head?.phone
                            ?? "Номер"
                        )
                        .font(.title3)
                    }
                    .padding(.vertical, 20)
                    
                    Divider()
                    
                    Button {
                        UserDefaults.standard.removeObject(forKey: "token")
                        UserDefaults.standard.removeObject(forKey: "userId")
                        UserDefaults.standard.removeObject(forKey: "fullName")
                        UserDefaults.standard.removeObject(forKey: "curatedGroup")
                        roleID = 0
                        isLoggedIn = false
                    } label: {
                        Text("Выйти из профиля")
                            .font(.title2)
                            .fontWeight(.semibold)
                            .foregroundColor(.red)
                    }
                    .padding(.top, 20)
                    .padding(.bottom, 25)
                }
                .frame(maxWidth: 320)
                .background(glassBg)
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                .cornerRadius(25)
                .padding(.bottom, 24)
                
                Spacer()
                
                Button {
                    dismiss()
                } label: {
                    Text("Назад")
                        .font(.title2)
                        .foregroundColor(.black)
                        .fontWeight(.semibold)
                        .frame(width: 250, height: 60)
                        .background(glassBg)
                        .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                        .cornerRadius(20)
                }
                
                Spacer()
            }
        }
        .navigationBarBackButtonHidden(true)
        .onAppear {
            viewModel.loadTeacherProfileIfNeeded()
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

#Preview {
    ProfileEducator(viewModel: TeacherViewModel())
}
