import SwiftUI

struct ContentView: View {

    @State private var email = ""
    @State private var password = ""

    var body: some View {

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
                    .padding()
                    .background(glassField)

                SecureField("Пароль...", text: $password)
                    .padding()
                    .background(glassField)
                
                Spacer()

                Button {

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
            .shadow(color: .black.opacity(0.25), radius: 20, x: 0, y: 10)
    }

    var glassField: some View {
        RoundedRectangle(cornerRadius: 16)
            .fill(Color(red: 217/255, green: 217/255, blue: 217/255).opacity(0.85))
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(Color.white.opacity(0.4))
            )
            .shadow(color: .black.opacity(0.1), radius: 6, y: 3)
    }
}

#Preview {
    ContentView()
}
