### This is an AI Project!
# Tactical Villagers (战术村民)

一个为 Minecraft Forge 1.20.1 开发的模组，保留了原版村民全功能 AI 的同时，为他们添加了战术武装、状态机算法、聊天互动和职业训练系统。

## 特性
- **全功能村民**：保留原版交易、工作、睡觉等功能。
- **简易战术算法**：基于状态机（State Machine），非大语言模型。
- **武装职业**：新增警察、特警、军队三种武装职业。
- **模组物品交互**：村民可自主使用末影珍珠、药水等物品。
- **聊天互动**：会在聊天框发送预设的战术指令或日常闲聊。
- **特殊装备**：包含普通枪械和电击/麻醉枪。

## 环境要求
- Minecraft 1.20.1
- Forge 47.x+
- JDK 17
- 前置模组: GeckoLib (4.x), Curios (5.x)

## 构建说明
本项目**不需要本地安装 Gradle 或 Maven**。
1. Fork 或 Push 代码到 GitHub。
2. 项目已配置 `.github/workflows/build.yml`。
3. 每次 Push 后，GitHub Actions 会自动运行并编译出 `.jar` 文件。
4. 在 Actions 运行详情页底部的 `Artifacts` 中下载编译好的模组。

## 开源协议
本项目采用 [MIT License](LICENSE) 协议。