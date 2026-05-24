import SwiftUI
//Первый экран, убрать QR, добавить сканер
struct EducatorFirstScreen: View {
    
    @State private var selectedPage: Int = 0
    @State private var goToProfile: Bool = false
    
    var body: some View {
        
        NavigationStack {
            
            ZStack {
                
                Image("background")
                    .resizable()
                    .scaledToFill()
                    .ignoresSafeArea()
                
                VStack {
                    
                    if selectedPage == 0 {
                        HStack {
                            
                            VStack(alignment: .leading) {
                                //После БД изменить
                                Text("Сейсекулова Сауле")
                                    .font(.title3)
                                    .fontWeight(.semibold)
                                //После БД изменить
                                Text("ИС22-4Б")
                                    .font(.subheadline)
                            }
                            
                            Spacer()
                            
                            Button {
                                goToProfile = true
                            } label: {
                                Circle()
                                    .fill(Color.white)
                                    .frame(width: 45, height: 45)
                            }
                        }
                        .padding(.top, 60)
                        .padding(.horizontal, 30)
                    }
                    
                    Spacer()
                    
                    Group {
                        switch selectedPage {
                        case 0:
                            QRPageE()
                        case 1:
                            EducatorSecondScreen()
                        case 2:
                            EducatorThirdScreen1(selectedPage: $selectedPage)
                        case 3:
                            EducatorThirdScreen2(selectedPage: $selectedPage)
                        case 4:
                            EducatorThirdScreen3(selectedPage: $selectedPage)
                        case 5:
                            EducatorThirdScreen4(selectedPage: $selectedPage)
                        default:
                            QRPageE()
                        }
                    }
                    
                    Spacer()
                    
                    BottomNavigationE(selectedPage: $selectedPage)
                        .padding(.bottom, 40)
                }
                .animation(.easeInOut(duration: 0.2), value: selectedPage)
            }
            
            .navigationDestination(isPresented: $goToProfile) {
                ProfileEducator()
            }
        }
    }
}

struct BottomNavigationE: View {
    
    @Binding var selectedPage: Int
    
    var body: some View {
        
        HStack(spacing: 40) {
            
            NavButtonE(icon: "iconsb", index: 0, selectedPage: $selectedPage)
            
            NavButtonE(icon: "iconsb1", index: 1, selectedPage: $selectedPage)
            
            NavButtonE(icon: "iconsb2", index: 2, selectedPage: $selectedPage)
        }
    }
}

struct NavButtonE: View {
    
    let icon: String
    let index: Int
    @Binding var selectedPage: Int
    
    let size: CGFloat = 85
    
    var isSelected: Bool {
        selectedPage == index
    }
    
    var body: some View {
        
        Button {
            selectedPage = index
        } label: {
            
            ZStack {
                
                RoundedRectangle(cornerRadius: size * 0.4)
                    .fill(.ultraThinMaterial)
                    .opacity(0.35)
                    .frame(width: size, height: size)
                    .background(
                        RoundedRectangle(cornerRadius: size * 0.4)
                            .fill(Color.white.opacity(0.2))
                    )
                    .overlay(
                        RoundedRectangle(cornerRadius: size * 0.4)
                            .stroke(Color.white.opacity(0.4), lineWidth: 1)
                    )
                    .shadow(color: Color.black.opacity(0.15), radius: 6, x: 0, y: 4)
                
                Image(icon)
                    .resizable()
                    .scaledToFit()
                    .frame(width: size * 0.5)
            }
        }
        .overlay(
            RoundedRectangle(cornerRadius: size * 0.4)
                .stroke(isSelected ? Color.black.opacity(0.8) : Color.clear, lineWidth: 3.5)
                .frame(width: size, height: size)
        )
        .padding(.bottom, 18)
        .animation(.easeInOut(duration: 0.15), value: isSelected)
    }
}

struct QRPageE: View {
    
    @State private var scannedCode: String = "Сканируйте QR"
    
    var body: some View {
        
        VStack(spacing: 25) {
            
            ZStack {
                
                QRScannerView(scannedCode: $scannedCode)
                    .frame(width: 320, height: 320)
                    .clipShape(RoundedRectangle(cornerRadius: 30))
                    .overlay(
                        RoundedRectangle(cornerRadius: 30)
                            .stroke(Color.white.opacity(0.7), lineWidth: 2)
                    )
                
                RoundedRectangle(cornerRadius: 20)
                    .stroke(Color.green, lineWidth: 3)
                    .frame(width: 220, height: 220)
            }
            
            Text(scannedCode)
                .font(.headline)
                .multilineTextAlignment(.center)
                .padding(.horizontal)
                .foregroundColor(.white)
        }
    }
}


#Preview {
    EducatorFirstScreen()
}
