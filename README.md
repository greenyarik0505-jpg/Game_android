# 🚀 Gravity Nomad

> **2D Sci-Fi Physics Arcade** — Исследуйте процедурные планеты, управляйте динамической гравитацией, улучшайте модульные вездеходы и преодолевайте опасный космический рельеф!

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin_2.0-purple.svg)
![UI](https://img.shields.io/badge/UI-Jetpack_Compose_M3-blue.svg)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2F%20MVVM-orange.svg)
![License](https://img.shields.io/badge/License-MIT-brightgreen.svg)

---

## 📦 Скачать готовый Release APK

Собрано с полной оптимизацией, подписью и подготовленными ресурсами:

* 📥 **[Скачать Release APK (Gofile)](https://gofile.io/d/o5F8R4)**
* 🚀 **[Зеркало 1 (Catbox Litterbox)](https://litter.catbox.moe/7iu9er.apk)**
* ⚡ **[Зеркало 2 (Tmpfiles)](https://tmpfiles.org/dl/wqwARVNAlI8Y/app-release.apk)**

---

## ✨ Особенности игры

- 🌌 **Динамическая гравитация**: Переключение векторов гравитации и активация энерго-щита на лету для преодоления мертвых петель и крутых обрывов.
- ⚙️ **Модульные вездеходы**: Сборка, тюнинг и апгрейд ходовой части, реактивного двигателя, аккумулятора и энергетических модулей в **Гараже**.
- 🛠️ **Дерево технологий (Tech Tree)**: Разблокируйте пассивные и активные эффекты: маневренность, турбо-буст, защита от перегрева и эффективный сбор кристаллов.
- 🎯 **Система квестов & Достижений**: Выполняйте ежедневные и сюжетные задачи на планетах для получения наград.
- 🎵 **Встроенный аудио-синтезатор**: Динамические звуковые эффекты двигателя, взрывов, кристаллов и перегрузки без сторонних тяжелых библиотек.
- 💾 **Локальное сохранение Room DB**: Надежное сохранение прогресса, монет, рекордов и настроек.

---

## 🛠 Технологический стек

* **Язык**: Kotlin 100%
* **UI**: Jetpack Compose (Material Design 3, Canvas Graphics)
* **Физический движок**: Кастомный 60 FPS 2D Vector Physics Engine (подвеска, трение, инерция, повреждение модулей, частицы)
* **База данных**: Room Database + KSP
* **Аудио**: Android AudioTrack Realtime PCM Synthesizer

---

## 🔄 Синхронизация с GitHub в AI Studio

Чтобы отправить код и этот `README.md` в репозиторий `greenyarik0505-jpg/Game_android`:

1. В правом верхнем углу нажмите кнопку **`GitHub`** (как на вашем скриншоте).
2. В поле **Commit message** введите сообщение (например: `Release build v1.0 with complete README and UI polish`).
3. Нажмите кнопку **`Commit and push`** (или зеленый чекмарк).

---

## 💻 Сборка из исходного кода

```bash
# Клонирование репозитория
git clone https://github.com/greenyarik0505-jpg/Game_android.git

# Переход в директорию
cd Game_android

# Сборка Release APK
./gradlew assembleRelease
```
