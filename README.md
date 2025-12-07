# Universal Mod of Infinite Flame Team (UMOIFT)

[使用说明](USAGE_GUIDE.md)
    
## 项目简介

UMOIFT 是一个为 Minecraft 1.20.1 开发的 Forge 模组，由 Infinite Flame Team 团队开发。该模组提供了丰富的功能，包括彩色灯具系统、领地管理、对话框系统等，旨在增强游戏体验。

## 主要功能

### 🌈 彩色灯具系统
- **16种颜色灯具**：白色、橙色、品红色、淡蓝色、黄色、黄绿色、粉色、灰色、淡灰色、青色、紫色、蓝色、棕色、绿色、红色、黑色
- **智能照明**：灯具可以根据状态自动调节亮度（开启时亮度为15，关闭时为0）
- **创意模式集成**：灯具会自动添加到功能方块、建筑方块和红石方块标签页中

### 🏰 领地管理系统
- **领地保护**：防止其他玩家在你的领地内进行未经授权的操作
- **移动控制**：管理玩家在领地内的移动权限
- **数据持久化**：领地数据会自动保存，服务器重启后仍然有效

### 💬 对话框系统
- **官方对话框**：支持自定义对话框内容
- **网络同步**：对话框内容通过网络同步到客户端
- **动态重载**：支持运行时重新加载对话框配置

### 🌍 多语言支持
- **简体中文** (zh_cn)
- **繁体中文** (zh_tw)
- **香港繁体** (zh_hk)
- **文言文** (lzh)

## 技术特性

- **Minecraft 版本**: 1.20.1
- **Forge 版本**: 47.4.10
- **Java 版本**: 17
- **构建工具**: Gradle with ForgeGradle
- **映射表**: Official Mojang Mappings
- **Mixin 支持**: 启用 Mixin 进行更深入的修改

## 开发团队

**Infinite Flame Team** 成员：
- **DeepSeek AI**: 开发、代码编写
- **Hei_wan_Feng**: 开发、代码编写、调试、资源管理、其他

**[合作]Ethernos Studio**: 协作开发、技术支持

## 构建和安装

### 开发环境搭建
```bash
# 克隆项目
git clone [项目地址]

# 进入项目目录
cd umoift

# 构建项目
./gradlew build

# 运行客户端测试
./gradlew runClient

# 运行服务器测试
./gradlew runServer
```

### 安装模组
1. 确保已安装 Minecraft 1.20.1 和对应版本的 Forge
2. 将构建好的 `umoift-*.jar` 文件放入 Minecraft 的 `mods` 文件夹
3. 启动游戏即可

## 命令系统

模组提供了以下命令：
- `/dialog`: 对话框相关命令
- `/claim`: 领地管理相关命令

## 配置和自定义

- 所有方块和物品的配置文件位于 `src/main/resources/data/umoift/`
- 语言文件位于 `src/main/resources/assets/umoift/lang/`
- 模型和纹理位于 `src/main/resources/assets/umoift/models/` 和 `src/main/resources/assets/umoift/textures/`

## 注意事项

⚠️ **重要提醒**: 部分功能可能尚未经过充分测试，如发现问题请及时提交 Issue。

## 联系方式

如有问题或建议，可以发送邮件至：Hei_wan_Feng@outlook.com
（请注意，邮件可能不会及时回复，但会被看到）

## 许可证

本项目采用 **All Rights Reserved** 许可证，保留所有权利。

---

**Infinite Flame Team** - 为 Minecraft 创造无限可能
