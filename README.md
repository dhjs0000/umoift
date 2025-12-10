# Universal Mod of Infinite Flame Team - Classic Edition (UMOIFT-Classic)

[📖 使用指南](USAGE_GUIDE.md)

## 项目概述

UMOIFT（Universal Mod of Infinite Flame Team）是一款专为 Minecraft 1.20.1 开发的专业级 Forge 模组，由 Infinite Flame Team 精心打造。本模组集成了先进的彩色照明系统、完善的领地管理机制以及灵活的对话框交互系统，致力于为玩家提供更加丰富的游戏体验。

![彩色灯具系统演示](/images/colored_lights.png)

## 核心功能特性

### 🌈 彩色照明系统
- **全色谱支持**：提供16种标准颜色灯具（白色、橙色、品红色、淡蓝色、黄色、黄绿色、粉色、灰色、淡灰色、青色、紫色、蓝色、棕色、绿色、红色、黑色）
- **动态亮度调节**：智能照明控制，开启状态亮度15级，关闭状态0级

### 🏰 领地管理系统
- **全方位保护机制**：防止未授权玩家在领地内进行破坏、建造等操作
- **精细化权限控制**：支持移动权限、交互权限等多维度管理
- **数据持久化存储**：领地配置自动保存，服务器重启后配置不丢失
- **实时性能优化**：高效的领地检测算法，确保服务器性能

### 💬 交互式对话系统
- **官方对话框支持**：完全自定义的对话框内容和样式
- **网络同步机制**：对话框内容实时同步至所有客户端
- **动态配置重载**：支持运行时热更新对话框配置
- **多场景应用**：适用于任务系统、教程引导、剧情展示等

### 🌍 国际化支持
- **简体中文** (zh_cn) - 完整本地化支持
- **繁体中文** (zh_tw) - 传统中文界面
- **香港繁体** (zh_hk) - 地区化繁体支持
- **文言文** (lzh) - 古典中文支持

## 技术规格

| 技术要素 | 规格说明 |
|---------|---------|
| Minecraft 版本 | 1.20.1 |
| Forge 版本 | 47.4.10 |
| Java 版本 | 17 |
| 构建工具 | Gradle with ForgeGradle |
| 映射表 | Official Mojang Mappings |
| 核心框架 | Mixin 支持深度修改 |
| 当前版本 | 0.0.0.5 |

## 开发团队

### 核心开发团队 - Ethernos Studio
- **dhjs0000**：首席开发工程师、系统架构设计、性能优化、资源管理

#### 原始团队贡献者
- **Hei_wan_Feng**：基本项目监制

### 历史贡献 - Infinite Flame Team
- **DeepSeek AI**：核心算法开发、代码实现
- **Hei_wan_Feng**：功能开发、调试优化、资源管理

## 快速开始

### 开发环境配置

```bash
# 克隆项目仓库
git clone [repository-url]
cd umoift

# 构建项目
./gradlew build

# 运行客户端测试
./gradlew runClient

# 运行服务器测试
./gradlew runServer
```

### 安装部署

1. **环境准备**：确保已安装 Minecraft 1.20.1 和对应版本的 Forge
2. **模组安装**：将构建生成的 `umoift-*.jar` 文件复制至 Minecraft `mods` 目录
3. **启动验证**：启动游戏确认模组加载成功

## 命令系统

模组提供以下管理命令：

| 命令 | 功能描述 |
|-----|---------|
| `/dialog` | 对话框系统管理（创建、编辑、删除） |
| `/claim` | 领地系统管理（创建、配置、权限设置） |

## 配置系统

### 文件结构
```
src/main/resources/
├── data/umoift/          # 方块和物品配置
├── assets/umoift/lang/   # 多语言文件
├── assets/umoift/models/ # 模型定义
└── assets/umoift/textures/ # 纹理资源
```

### 自定义配置
- 支持通过数据包扩展模组功能
- 提供完整的JSON配置格式文档
- 支持运行时配置重载

## 技术支持

### 问题反馈
如遇技术问题或发现Bug，请通过以下方式联系：

📧 **邮箱**：studio@ethernos.cn  
*（我们会在收到邮件后尽快处理，感谢您的耐心）*

### 社区支持
- 关注我们的GitHub获取最新更新
- 参与社区讨论，共同改进模组功能

## 许可证说明

本项目基于 **BSD 3-Clause** 许可证开源，允许：

- ✅ 自由使用和修改代码
- ✅ 商业和非商业用途
- ✅ 分发和再发布

请在使用时遵守许可证条款，保留原始版权声明。

---

<div align="center">

##### **Infinite Flame Team** - 为 Minecraft 创造无限可能

#### **Ethernos Studio** - 在代码的宇宙中，我们是点燃星系的火花
>⚡ 创新 · 🤝 协作 · 🏆 卓越 ⚡

[Ethernos Studio Github](github.com/Ethernos-Studio)
</div>