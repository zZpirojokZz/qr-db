import SwiftUI

struct AdministracionThirdScreen1: View {
    
    @Binding var selectedPage: Int
    @State private var groupName: String = ""
    
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
                    .onSubmit {
                        if !groupName.isEmpty {
                            selectedPage = 3
                        }
                    }
                    .padding()
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
            .padding(.horizontal, 24)
            .padding(.top, 190)
        
            Button {
                selectedPage = 5
            } label: {
                Text("Перейти к группе\nИС22-4Б") //После БД изменить
                    .font(.system(size: 18, weight: .medium))
                    .foregroundColor(.black)
                    .multilineTextAlignment(.center)
                    .padding()
                    .frame(maxWidth: 260)
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
            .padding(.top, 170)
            
            Spacer()
        }
        }
    }


#Preview {
    AdministracionThirdScreen1(selectedPage: .constant(0))
}
