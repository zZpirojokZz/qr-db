import SwiftUI

struct ProfileStudent: View {
    
    @Environment(\.dismiss) var dismiss
    
    var body: some View {
        
        ZStack {
            
            Image("background")
                .resizable()
                .scaledToFill()
                .ignoresSafeArea()
            
            VStack(spacing: 20) {
                
                Spacer()
                
                VStack(spacing: 5) {
                    
                    Circle()
                        .fill(Color.white.opacity(0.9))
                        .frame(width: 90, height: 90)
                        .shadow(radius: 0.5)
                        .padding(.vertical, 20)
                    //После БД изменить
                    Text("Пивнев Игорь")
                        .font(.title2)
                        .fontWeight(.semibold)
                        .padding(.bottom, 1)
                    
                    Text("староста")
                        .font(.subheadline)
                        .padding(.bottom, 10)
                    
                    Divider()
                    //После БД изменить
                    Text("ИС22-4Б")
                        .font(.headline)
                        .padding(.bottom, 15)
                        .padding(.top, 10)
                }
                .frame(maxWidth: 320)
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
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                .cornerRadius(25)
                
                VStack() {
                    
                    VStack {
                        Text("Сауле Бактыбаевна")//После БД изменить
                            .font(.title2)
                            .fontWeight(.semibold)
                        Text("+7(777)777-77-77")//После БД изменить
                            .font(.title3)
                    }
                    .padding(.top, 25)
                    .padding(.bottom, 20)
                    
                    Divider()
                    
                    VStack {
                        Text("Джаманкузова Молдир")//После БД изменить
                            .font(.title2)
                            .fontWeight(.semibold)
                        Text("8(888)888-88-88")//После БД изменить
                            .font(.title3)
                    }
                    .padding(.vertical, 20)
                    
                    Divider()
                    
                    Button {
                    } label: {
                        Text("Выйти из профиля")
                            .font(.title2)
                            .foregroundColor(.red)
                            .font(.headline)
                    }
                    .padding(.top, 20)
                    .padding(.bottom, 25)
                }
                .frame(maxWidth: 320)
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
                .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                .cornerRadius(25)
                
                Spacer()
                
                Button {
                    dismiss()
                } label: {
                    Text("Назад")
                        .font(.title2)
                        .foregroundColor(.black)
                        .fontWeight(.semibold)
                        .frame(width: 250, height: 60)
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
                        .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                        .cornerRadius(20)
                }
                
                Spacer()
            }
        }
        .navigationBarBackButtonHidden(true)
    }
}

#Preview {
    ProfileStudent()
}
