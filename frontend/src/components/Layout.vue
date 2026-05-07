<template>
  <div class="layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <el-icon class="logo-icon"><Reading /></el-icon>
        <span v-if="!isCollapsed" class="logo-text">图书管理系统</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        class="sidebar-menu"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-menu-item :index="'/' + route.path">
            <el-icon><component :is="route.meta.icon" /></el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </aside>
    
    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="header-left">
          <el-button 
            :icon="isCollapsed ? 'Expand' : 'Fold'" 
            @click="toggleSidebar"
            class="collapse-btn"
          />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta.title !== '仪表盘'">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                {{ nickname.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="user-name">{{ nickname }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <!-- 内容区 -->
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { 
  Reading, Odometer, Document, Menu, User, 
  ChatDotRound, ArrowDown, SwitchButton 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapsed = ref(false)

const nickname = computed(() => userStore.nickname)
const isAdmin = computed(() => userStore.isAdmin)
const currentRoute = computed(() => route)
const activeMenu = computed(() => route.path)

// 获取菜单路由
const menuRoutes = computed(() => {
  const allRoutes = router.options.routes.find(r => r.path === '/')?.children || []
  return allRoutes.filter(r => {
    if (r.meta?.hidden) return false
    if (r.meta?.admin && !isAdmin.value) return false
    return true
  })
})

// 切换侧边栏
function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
}

// 处理下拉菜单命令
function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch {
    // 取消
  }
}
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-page);
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1e3a5f 0%, #152238 100%);
  transition: width var(--transition-base);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: white;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  font-size: 28px;
  color: var(--primary-light);
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 100%;
}

:deep(.el-menu) {
  background: transparent;
  border: none;
}

:deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.7);
  height: 52px;
  line-height: 52px;
  margin: 8px 12px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

:deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
  color: white;
  transform: translateX(4px);
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, var(--primary-color) 0%, #4facfe 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.4);
  font-weight: 600;
}

:deep(.el-menu-item .el-icon) {
  font-size: 20px;
  margin-right: 12px;
  transition: transform 0.3s ease;
}

:deep(.el-menu-item.is-active .el-icon) {
  transform: scale(1.1);
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.5));
}

/* 主容器 */
.main-container {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
  transition: margin-left var(--transition-base);
}

.sidebar.collapsed + .main-container {
  margin-left: 64px;
}

/* 顶部导航 */
.header {
  height: 64px;
  background: white;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  border: none;
  background: var(--bg-light);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: var(--radius-md);
  transition: background var(--transition-fast);
}

.user-info:hover {
  background: var(--bg-light);
}

.user-avatar {
  background: var(--gradient-primary);
  color: white;
  font-weight: 600;
}

.user-name {
  color: var(--text-primary);
  font-weight: 500;
}

/* 内容区 */
.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

<!-- 全局样式：覆盖 Element Plus 折叠菜单内部样式 -->
<style>
.sidebar.collapsed .el-menu--collapse .el-menu-item,
.sidebar.collapsed .el-menu--collapse .el-menu-item .el-tooltip__trigger {
  padding: 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.sidebar.collapsed .el-menu--collapse .el-menu-item {
  margin: 6px auto !important;
  width: 44px !important;
  min-width: 44px !important;
  height: 44px !important;
  line-height: 44px !important;
  border-radius: 10px !important;
}

.sidebar.collapsed .el-menu--collapse .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.08) !important;
}

.sidebar.collapsed .el-menu--collapse .el-menu-item.is-active {
  background: var(--primary-color, #409eff) !important;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.35);
}

.sidebar.collapsed .el-menu--collapse .el-menu-item .el-icon {
  margin: 0 !important;
  padding: 0 !important;
  font-size: 20px !important;
  width: auto !important;
}
</style>
