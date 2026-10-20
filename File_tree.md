tactical-villagers/
├── .github/
│   └── workflows/
│       └── build.yml                 # GitHub Actions 自动编译脚本
├── gradle/
│   └── wrapper/                      # 必须包含 gradle-wrapper.jar 和 properties
├── src/
│   └── main/
│       ├── java/com/yourname/tacticalvillagers/
│       │   ├── TacticalVillagersMod.java     # 主类
│       │   ├── registry/
│       │   │   ├── ModEntities.java          # 实体注册
│       │   │   ├── ModItems.java             # 物品注册
│       │   │   └── ModEffects.java           # 药水效果注册
│       │   ├── entity/
│       │   │   ├── TacticalVillager.java     # 全功能带枪村民基类
│       │   │   ├── PoliceVillager.java       # 警察
│       │   │   ├── SwatVillager.java         # 特警
│       │   │   └── ArmyVillager.java         # 军队
│       │   ├── entity/projectile/
│       │   │   ├── BulletProjectile.java     # 普通子弹
│       │   │   └── TaserProjectile.java      # 电击弹
│       │   └── effect/
│       │       └── ParalysisEffect.java      # 麻痹/麻醉效果
│       └── resources/
│           ├── META-INF/
│           │   └── mods.toml                 # 模组信息
│           └── pack.mcmeta                   # 资源包信息
├── build.gradle
├── gradle.properties
├── settings.gradle
├── gradlew
├── gradlew.bat
├── LICENSE
└── README.md