import SwiftUI

struct StudentFirstScreen: View {
    
    @State private var selectedPage: Int = 0
    @State private var goToProfile: Bool = false
    @StateObject private var viewModel = StudentViewModel()
    
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
                                
                                Text(
                                    viewModel.profile?.student.full_name
                                    ?? UserDefaults.standard.string(forKey: "fullName")
                                    ?? "Загрузка..."
                                )
                                .font(.title3)
                                .fontWeight(.semibold)
                                
                                Text(
                                    viewModel.profile?.student.group_name
                                    ?? UserDefaults.standard.string(forKey: "groupName")
                                    ?? ""
                                )
                                .font(.subheadline)
                            }
                            
                            Spacer()
                            
                            Button {
                                goToProfile = true
                            } label: {
                                Image("iconprof45")
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
                            QRPageS(
                                studentId: UserDefaults.standard.integer(
                                    forKey: "userId"
                                )
                            )
                        case 1:
                            StudentSecondScreen()
                        case 2:
                            StudentThirdScreen()
                        default:
                            QRPageS(
                                studentId: UserDefaults.standard.integer(
                                    forKey: "userId"
                                )
                            )
                        }
                    }
                    
                    Spacer()
                    
                    BottomNavigationS(selectedPage: $selectedPage)
                        .padding(.bottom, 40)
                }
                .animation(.easeInOut(duration: 0.2), value: selectedPage)
            }
            .onAppear {
                viewModel.loadProfileIfNeeded()
            }
            .navigationDestination(isPresented: $goToProfile) {
                ProfileStudent(viewModel: viewModel)
            }
        }
    }
}

struct BottomNavigationS: View {
    
    @Binding var selectedPage: Int
    
    var body: some View {
        
        HStack(spacing: 40) {
            NavButtonS(icon: "iconsb", index: 0, selectedPage: $selectedPage)
            NavButtonS(icon: "iconsb1", index: 1, selectedPage: $selectedPage)
            NavButtonS(icon: "iconsb2", index: 2, selectedPage: $selectedPage)
        }
    }
}

struct NavButtonS: View {
    
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

struct QRPageS: View {
    
    let studentId: Int
    
    @State private var scannedCode = ""
    @State private var resultMessage = "Отсканируйте QR-код"
    @State private var isProcessing = false
    @State private var lastScannedAt: Date? = nil
    
    var body: some View {
        
        VStack(spacing: 25) {
            
            QRScannerView(scannedCode: $scannedCode)
                .frame(width: 310, height: 310)
                .clipShape(RoundedRectangle(cornerRadius: 30))
            
            Text(resultMessage)
                .font(.headline)
                .foregroundColor(.white)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 20)
        }
        .onChange(of: scannedCode) { _, newValue in
            handleScan(code: newValue)
        }
    }
    
    private func handleScan(code: String) {
        
        guard !code.isEmpty else { return }
        guard !isProcessing else { return }
        
        // Защита от повторного срабатывания на тот же QR в течение 3 сек
        if let last = lastScannedAt,
           Date().timeIntervalSince(last) < 3 {
            return
        }
        lastScannedAt = Date()
        
        guard studentId > 0 else {
            resultMessage = "Не найден ID студента"
            return
        }
        
        isProcessing = true
        resultMessage = "Отправка..."
        
        APIService.shared.markAttendance(
            qrCode: code,
            studentId: studentId
        ) { result in
            
            isProcessing = false
            scannedCode = ""
            
            switch result {
            case .success(let message):
                resultMessage = message
                UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                
            case .failure(let error):
                resultMessage = error.localizedDescription
            }
        }
    }
}

#Preview {
    StudentFirstScreen()
}
