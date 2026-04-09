# QR-DB Project - Sweep Configuration

## Исправленные проблемы

### 1. Ошибка в build.gradle.kts
**Проблема:** Неправильный синтаксис для `compileSdk`
```kotlin
compileSdk {
    version = release(36)
}
```

**Исправление:**
```kotlin
compileSdk = 36
```

### 2. Ошибка в AuthScreen.kt
**Проблема:** Использование `.value` на типе Float
```kotlin
fontSize = (14 * scaleX.value.coerceIn(0.8f, 1.2f)).sp
```

**Исправление:**
```kotlin
fontSize = (14 * scaleX.coerceIn(0.8f, 1.2f)).sp
```

## Структура проекта

- **Язык:** Kotlin
- **UI Framework:** Jetpack Compose
- **Build System:** Gradle (Kotlin DSL)
- **Min SDK:** 28
- **Target SDK:** 36
- **Compile SDK:** 36

## Основные зависимости

- androidx.navigation:navigation-compose:2.8.5
- com.google.code.gson:gson:2.10.1
- Jetpack Compose BOM: 2024.09.00

## Команды для сборки

```powershell
# Очистка и сборка проекта
./gradlew clean build

# Запуск приложения
./gradlew installDebug

# Проверка зависимостей
./gradlew dependencies
```

## Структура экранов

1. **AuthScreen** - Экран авторизации
2. **StudentScreen** - Экран студента с QR-кодом
3. **TeacherScreen** - Экран преподавателя со сканером
4. **AdminScreen** - Экран администратора

## Цветовая палитра (из Figma)

- FigmaBlack: #000000
- FigmaNearBlack: #1A1A1A
- FigmaRed: #B71B1B
- FigmaWhite: #FFFFFF
- FigmaGrey1: #D0D0D0
- FigmaGrey2: #D9D9D9
