import SwiftUI

struct EducatorFirstScreen: View {
    
    @State private var selectedPage: Int = 0
    @State private var goToProfile: Bool = false
    @StateObject private var viewModel = TeacherViewModel()
    
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
                                    viewModel.teacherProfile?.teacher.full_name
                                    ?? UserDefaults.standard.string(forKey: "fullName")
                                    ?? "Загрузка..."
                                )
                                .font(.title3)
                                .fontWeight(.semibold)
                                
                                Text(
                                    viewModel.teacherProfile?.curated_group
                                    ?? UserDefaults.standard.string(forKey: "curatedGroup")
                                    ?? "—"
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
                            QRPageE(viewModel: viewModel)
                        case 1:
                            EducatorSecondScreen()
                        case 2:
                            EducatorThirdScreen1(
                                selectedPage: $selectedPage
                            )
                        case 3:
                            EducatorThirdScreen2(
                                selectedPage: $selectedPage
                            )
                        case 4:
                            EducatorThirdScreen3(
                                selectedPage: $selectedPage
                            )
                        case 5:
                            EducatorThirdScreen4(
                                selectedPage: $selectedPage
                            )
                        default:
                            QRPageE(viewModel: viewModel)
                        }
                    }
                    
                    Spacer()
                    
                    BottomNavigationE(selectedPage: $selectedPage)
                        .padding(.bottom, 40)
                }
                .animation(
                    .easeInOut(duration: 0.2),
                    value: selectedPage
                )
            }
            .onAppear {
                viewModel.loadTeacherProfile()
                viewModel.startPolling()
            }
            .onDisappear {
                viewModel.stopPolling()
            }
            .navigationDestination(isPresented: $goToProfile) {
                ProfileEducator(viewModel: viewModel)
            }
        }
    }
}

struct BottomNavigationE: View {
    
    @Binding var selectedPage: Int
    
    var body: some View {
        
        HStack(spacing: 40) {
            
            NavButtonE(
                icon: "iconsb",
                index: 0,
                selectedPage: $selectedPage
            )
            
            NavButtonE(
                icon: "iconsb1",
                index: 1,
                selectedPage: $selectedPage
            )
            
            NavButtonE(
                icon: "iconsb2",
                index: 2,
                selectedPage: $selectedPage
            )
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
                            .stroke(
                                Color.white.opacity(0.4),
                                lineWidth: 1
                            )
                    )
                    .shadow(
                        color: Color.black.opacity(0.15),
                        radius: 6,
                        x: 0,
                        y: 4
                    )
                
                Image(icon)
                    .resizable()
                    .scaledToFit()
                    .frame(width: size * 0.5)
            }
        }
        .overlay(
            RoundedRectangle(cornerRadius: size * 0.4)
                .stroke(
                    isSelected
                    ? Color.black.opacity(0.8)
                    : Color.clear,
                    lineWidth: 3.5
                )
                .frame(width: size, height: size)
        )
        .padding(.bottom, 18)
        .animation(
            .easeInOut(duration: 0.15),
            value: isSelected
        )
    }
}

struct QRPageE: View {
    
    @ObservedObject var viewModel: TeacherViewModel
    @State private var qrVersion: Int = 0
    @State private var qrImage: UIImage? = nil
    @State private var qrScale: CGFloat = 1.0
    
    var body: some View {
        
        VStack(spacing: 20) {
            
            if let lesson = viewModel.currentLesson {
                
                if let image = qrImage {
                    
                    Image(uiImage: image)
                        .interpolation(.none)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 300, height: 300)
                        .cornerRadius(12)
                        .scaleEffect(qrScale)
                        .onTapGesture {
                            
                            withAnimation(.easeInOut(duration: 0.15)) {
                                qrScale = 0.92
                            }
                            
                            qrVersion += 1
                            regenerateQR(lesson: lesson)
                            
                            DispatchQueue.main.asyncAfter(
                                deadline: .now() + 0.15
                            ) {
                                withAnimation(.easeInOut(duration: 0.15)) {
                                    qrScale = 1.0
                                }
                            }
                        }
                    
                } else {
                    
                    ProgressView()
                        .frame(width: 300, height: 300)
                }
                
            } else {
                
                Text("У вас нет занятий сейчас")
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.black)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 40)
                    .padding(.vertical, 20)
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
                    .padding(.top, 35)
            }
        }
        .padding(.bottom, 68)
        .onAppear {
            if let lesson = viewModel.currentLesson {
                regenerateQR(lesson: lesson)
            }
        }
        .onChange(of: viewModel.currentLesson?.lesson_id) {
            if let lesson = viewModel.currentLesson {
                qrVersion = 0
                regenerateQR(lesson: lesson)
            } else {
                qrImage = nil
            }
        }
    }
    
    private func regenerateQR(lesson: TeacherLesson) {
        
        let userId = UserDefaults.standard.integer(
            forKey: "userId"
        )
        
        let qrData = "\(lesson.lesson_id)_\(userId)_\(qrVersion)"
        
        qrImage = QRCodeGenerator.generate(
            from: qrData,
            size: 900
        )
    }
}

#Preview {
    EducatorFirstScreen()
}
