package com.example.data.db

import com.example.engine.ModuleCategory
import com.example.engine.ModuleRarity
import com.example.engine.VehicleModule

object DefaultModulesCatalog {
    val ALL_CATALOG_MODULES = listOf(
        // CHASSIS
        VehicleModule(
            id = "chassis_scout",
            name = "Корпус «Разведчик»",
            category = ModuleCategory.CHASSIS,
            rarity = ModuleRarity.COMMON,
            description = "Лёгкое композитное шасси для быстрых исследовательских рейдов.",
            weight = 12f,
            powerOutput = 0f,
            energyCap = 20f,
            coolingRate = 1.0f,
            maxHp = 100f
        ),
        VehicleModule(
            id = "chassis_heavy",
            name = "Бастион «Титан»",
            category = ModuleCategory.CHASSIS,
            rarity = ModuleRarity.RARE,
            description = "Усиленный титановый бронекорпус с тяжёлыми демпферами ударов.",
            weight = 25f,
            powerOutput = 0f,
            energyCap = 40f,
            coolingRate = 0.8f,
            maxHp = 250f,
            specialPerk = "+20% к стойкости к столкновениям"
        ),
        VehicleModule(
            id = "chassis_bio",
            name = "Живой корпус «Био-Сетка»",
            category = ModuleCategory.CHASSIS,
            rarity = ModuleRarity.EPIC,
            description = "Органическая биомеханическая рама, постепенно восстанавливающая целостность.",
            weight = 16f,
            powerOutput = 0f,
            energyCap = 60f,
            coolingRate = 1.2f,
            maxHp = 180f,
            specialPerk = "Пассивная регенерация корпуса"
        ),
        VehicleModule(
            id = "chassis_quantum",
            name = "Квантовая Сингулярность",
            category = ModuleCategory.CHASSIS,
            rarity = ModuleRarity.LEGENDARY,
            description = "Антигравитационный корпус со встроенным мини-ядром искривления пространства.",
            weight = 10f,
            powerOutput = 5f,
            energyCap = 100f,
            coolingRate = 1.5f,
            maxHp = 300f,
            specialPerk = "Иммунитет к урону от грави-аномалий"
        ),

        // ENGINE
        VehicleModule(
            id = "engine_ion",
            name = "Ионный двигатель Mk-I",
            category = ModuleCategory.ENGINE,
            rarity = ModuleRarity.COMMON,
            description = "Надёжный ионный ускоритель с низким выделением тепла.",
            weight = 8f,
            powerOutput = 25f,
            energyCap = 0f,
            coolingRate = 1.0f,
            maxHp = 60f
        ),
        VehicleModule(
            id = "engine_plasma",
            name = "Плазменный форсаж",
            category = ModuleCategory.ENGINE,
            rarity = ModuleRarity.RARE,
            description = "Высокотяговитый плазменный двигатель с огненным выхлопом.",
            weight = 14f,
            powerOutput = 45f,
            energyCap = 0f,
            coolingRate = 1.2f,
            maxHp = 90f,
            specialPerk = "+15% к максимальной скорости"
        ),
        VehicleModule(
            id = "engine_overdrive",
            name = "Тахионный овердрайв",
            category = ModuleCategory.ENGINE,
            rarity = ModuleRarity.EPIC,
            description = "Субатомный реактор, обеспечивающий молниеносный разгон.",
            weight = 10f,
            powerOutput = 70f,
            energyCap = 10f,
            coolingRate = 1.5f,
            maxHp = 120f,
            specialPerk = "Гипер-рывок при старте"
        ),
        VehicleModule(
            id = "engine_antimatter",
            name = "Двигатель на антиматерии",
            category = ModuleCategory.ENGINE,
            rarity = ModuleRarity.LEGENDARY,
            description = "Использует аннигиляцию антиматерии для бесконечной реактивной тяги.",
            weight = 8f,
            powerOutput = 100f,
            energyCap = 30f,
            coolingRate = 2.0f,
            maxHp = 180f,
            specialPerk = "Полное отсутствие перегрева на максимальном газу"
        ),

        // WHEELS / PROPULSION
        VehicleModule(
            id = "wheels_cyber",
            name = "Кибер-колёса «Вседорожник»",
            category = ModuleCategory.WHEELS,
            rarity = ModuleRarity.COMMON,
            description = "Двойные резино-композитные шины для отличного сцепления с грунтом.",
            weight = 6f,
            powerOutput = 5f,
            energyCap = 0f,
            coolingRate = 1.0f,
            maxHp = 50f
        ),
        VehicleModule(
            id = "wheels_magnet",
            name = "Магнитные траки",
            category = ModuleCategory.WHEELS,
            rarity = ModuleRarity.RARE,
            description = "Гусеницы с магнитным прилипанием к стенам и потолкам.",
            weight = 18f,
            powerOutput = 10f,
            energyCap = 0f,
            coolingRate = 0.9f,
            maxHp = 140f,
            specialPerk = "Магнитное сцепление со стенами и потолком"
        ),
        VehicleModule(
            id = "wheels_antigrav",
            name = "Антигравитационные подушки",
            category = ModuleCategory.WHEELS,
            rarity = ModuleRarity.EPIC,
            description = "Заменяет колёса репульсорными полями, парящими над лавой и токсинами.",
            weight = 5f,
            powerOutput = 20f,
            energyCap = 15f,
            coolingRate = 1.3f,
            maxHp = 100f,
            specialPerk = "Защита от вязкости лавы и слизи"
        ),
        VehicleModule(
            id = "wheels_biolegs",
            name = "Арахнидные био-ноги",
            category = ModuleCategory.WHEELS,
            rarity = ModuleRarity.LEGENDARY,
            description = "Адаптивные биомеханические ноги, перепрыгивающие препятствия и стабилизирующие трюки.",
            weight = 7f,
            powerOutput = 35f,
            energyCap = 20f,
            coolingRate = 1.8f,
            maxHp = 200f,
            specialPerk = "Авто-посадка после сальто и прыжковый импульс"
        ),

        // CORE
        VehicleModule(
            id = "core_fusion",
            name = "Микро-термоядерная ячейка",
            category = ModuleCategory.CORE,
            rarity = ModuleRarity.COMMON,
            description = "Стандартное ядро постоянной энергии.",
            weight = 5f,
            powerOutput = 0f,
            energyCap = 100f,
            coolingRate = 1.0f,
            maxHp = 50f
        ),
        VehicleModule(
            id = "core_zero_point",
            name = "Генератор нулевой точки",
            category = ModuleCategory.CORE,
            rarity = ModuleRarity.EPIC,
            description = "Выкачивает энергию из вакуума, стремительно восполняя заряд.",
            weight = 4f,
            powerOutput = 10f,
            energyCap = 250f,
            coolingRate = 1.6f,
            maxHp = 110f,
            specialPerk = "Авто-регенерация энергии в полете"
        ),

        // STABILIZER
        VehicleModule(
            id = "stabilizer_gyro",
            name = "Гироскопический стабилизатор",
            category = ModuleCategory.STABILIZER,
            rarity = ModuleRarity.COMMON,
            description = "Помогает удерживать баланс и тангаж в воздухе.",
            weight = 4f,
            powerOutput = 0f,
            energyCap = 0f,
            coolingRate = 1.0f,
            maxHp = 40f
        ),
        VehicleModule(
            id = "stabilizer_warp",
            name = "Варп-анкор гравитации",
            category = ModuleCategory.STABILIZER,
            rarity = ModuleRarity.LEGENDARY,
            description = "Мгновенно выравнивает ориентацию вездехода при любой смене гравитации.",
            weight = 3f,
            powerOutput = 15f,
            energyCap = 50f,
            coolingRate = 2.0f,
            maxHp = 150f,
            specialPerk = "Мгновенная ориентация к вектору гравитации"
        ),

        // UTILITY
        VehicleModule(
            id = "utility_drone",
            name = "Ремонтный дрон «Нанит»",
            category = ModuleCategory.UTILITY,
            rarity = ModuleRarity.RARE,
            description = "Выпускает нано-роботов для починки повреждённых компонентов во время заезда.",
            weight = 3f,
            powerOutput = 0f,
            energyCap = 20f,
            coolingRate = 1.1f,
            maxHp = 50f,
            specialPerk = "Активный ремонт вездехода"
        ),
        VehicleModule(
            id = "utility_shield",
            name = "Излучатель силовой барьера",
            category = ModuleCategory.UTILITY,
            rarity = ModuleRarity.EPIC,
            description = "Создаёт голубое силовое поле, поглощающее кинетические удары.",
            weight = 4f,
            powerOutput = 0f,
            energyCap = 40f,
            coolingRate = 1.4f,
            maxHp = 80f,
            specialPerk = "Активный защитный барьер"
        )
    )

    fun getModuleById(id: String): VehicleModule {
        return ALL_CATALOG_MODULES.find { it.id == id } ?: ALL_CATALOG_MODULES.first()
    }
}
