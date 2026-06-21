import SwiftUI

enum UserRole {
    case login
    case student
    case teacher
    case admin
}

struct Authotization: View {
    
    @AppStorage("isLoggedIn")
    private var isLoggedIn = false
    
    @AppStorage("roleID")
    private var roleID = 0
    
    @State private var currentRole: UserRole = .login
    
    @State private var email = ""
    @State private var password = ""
    @State private var errorMessage = ""
    
    var body: some View {
        
        Group {
            
            if isLoggedIn {
                
                switch currentRole {
                case .student:
                    StudentFirstScreen()
                    
                case .teacher:
                    EducatorFirstScreen()
                    
                case .admin:
                    AdministracionFirstScreen()
                    
                case .login:
                    loginView
                }
                
            } else {
                loginView
            }
        }
        .onAppear {
            updateCurrentRole()
        }
    }
    
    var loginView: some View {
        
        ZStack {
            
            Image("background")
                .resizable()
                .scaledToFill()
                .ignoresSafeArea()
            
            VStack(spacing: 25) {
                
                Text("Войдите в аккаунт")
                    .font(.title3)
                    .fontWeight(.semibold)
                    .padding(.top, 10)
                
                TextField("Электронная почта...", text: $email)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
                    .keyboardType(.emailAddress)
                    .padding()
                    .background(glassField)
                
                SecureField("Пароль...", text: $password)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
                    .padding()
                    .background(glassField)
                
                if !errorMessage.isEmpty {
                    Text(errorMessage)
                        .foregroundColor(.red)
                        .font(.footnote)
                        .multilineTextAlignment(.center)
                }
                
                Spacer()
                
                Button {
                    login()
                } label: {
                    Text("Войти")
                        .font(.title3)
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(glassField)
                }
                
            }
            .padding(50)
            .frame(maxWidth: 330)
            .frame(maxHeight: 460)
            .background(glassCard)
            .padding()
            
        }
    }
    
    private func login() {
        
        errorMessage = ""
        
        APIService.shared.login(
            email: email,
            password: password
        ) { result in
            
            switch result {
                
            case .success(let response):
                
                UserDefaults.standard.set(
                    response.token,
                    forKey: "token"
                )
                
                UserDefaults.standard.set(
                    response.user_id,
                    forKey: "userId"
                )
                
                UserDefaults.standard.set(
                    response.role_id,
                    forKey: "roleID"
                )
                
                UserDefaults.standard.set(
                    response.full_name,
                    forKey: "fullName"
                )
                
                UserDefaults.standard.set(
                    response.email,
                    forKey: "email"
                )
                
                UserDefaults.standard.set(
                    response.group_name,
                    forKey: "groupName"
                )
                
                roleID = response.role_id
                updateCurrentRole()
                isLoggedIn = true
                
                print("Успешный вход")
                print("token =", UserDefaults.standard.string(forKey: "token") ?? "nil")
                print("userId =", UserDefaults.standard.integer(forKey: "userId"))
                print("roleID =", UserDefaults.standard.integer(forKey: "roleID"))
                
            case .failure(let error):
                
                errorMessage = "Ошибка входа: \(error.localizedDescription)"
                print(error.localizedDescription)
            }
        }
    }
    
    private func updateCurrentRole() {
        guard isLoggedIn || roleID != 0 else {
            currentRole = .login
            return
        }
        
        switch roleID {
        case 1:
            currentRole = .student
        case 2:
            currentRole = .teacher
        case 3, 4:
            currentRole = .admin
        default:
            currentRole = .login
        }
    }
    
    var glassCard: some View {
        RoundedRectangle(cornerRadius: 30)
            .fill(Color.white.opacity(0.45))
            .overlay(
                RoundedRectangle(cornerRadius: 30)
                    .stroke(
                        LinearGradient(
                            colors: [
                                Color.white.opacity(0.7),
                                Color.white.opacity(0.1)
                            ],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ),
                        lineWidth: 1.5
                    )
            )
            .shadow(
                color: .black.opacity(0.25),
                radius: 20,
                x: 0,
                y: 10
            )
    }
    
    var glassField: some View {
        RoundedRectangle(cornerRadius: 16)
            .fill(
                Color(
                    red: 217 / 255,
                    green: 217 / 255,
                    blue: 217 / 255
                ).opacity(0.85)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(Color.white.opacity(0.4))
            )
            .shadow(
                color: .black.opacity(0.1),
                radius: 6,
                y: 3
            )
    }
}

#Preview {
    Authotization()
}
