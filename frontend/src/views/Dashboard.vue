<template>
  <div class="dashboard">
    <div class="page-header">
      <h1 class="page-title">仪表盘</h1>
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card" style="background: var(--gradient-primary)">
          <div class="stat-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-content">
            <div class="value">{{ stats.totalBooks || 0 }}</div>
            <div class="label">图书总数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card" style="background: var(--gradient-success)">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="value">{{ stats.totalUsers || 0 }}</div>
            <div class="label">用户总数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card" style="background: var(--gradient-cool)">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="value">{{ stats.borrowingCount || 0 }}</div>
            <div class="label">借阅中</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card" style="background: var(--gradient-warm)">
          <div class="stat-icon">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="value">{{ stats.overdueCount || 0 }}</div>
            <div class="label">已逾期</div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 今日统计 -->
    <el-row :gutter="20" class="today-row">
      <el-col :xs="24" :sm="12">
        <div class="today-card">
          <div class="today-icon borrow">
            <el-icon><Upload /></el-icon>
          </div>
          <div class="today-content">
            <div class="value">{{ stats.todayBorrow || 0 }}</div>
            <div class="label">今日借阅</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12">
        <div class="today-card">
          <div class="today-icon return">
            <el-icon><Download /></el-icon>
          </div>
          <div class="today-content">
            <div class="value">{{ stats.todayReturn || 0 }}</div>
            <div class="label">今日归还</div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h2 class="section-title">快捷操作</h2>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6">
          <div class="action-card" @click="$router.push('/books')">
            <el-icon class="action-icon"><Reading /></el-icon>
            <span>图书管理</span>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="action-card" @click="$router.push('/borrows')">
            <el-icon class="action-icon"><Document /></el-icon>
            <span>借阅记录</span>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="action-card" @click="$router.push('/barrages')">
            <el-icon class="action-icon"><ChatDotRound /></el-icon>
            <span>弹幕墙</span>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="action-card" @click="$router.push('/profile')">
            <el-icon class="action-icon"><Setting /></el-icon>
            <span>个人中心</span>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '@/api/dashboard'
import { 
  Reading, User, Document, Warning, 
  Upload, Download, ChatDotRound, Setting 
} from '@element-plus/icons-vue'

const stats = ref({})

onMounted(async () => {
  try {
    const res = await getStatistics()
    stats.value = res.data
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
})
</script>

<style scoped>
.dashboard {
  animation: fadeIn 0.5s ease;
}

.stat-row {
  margin-bottom: 24px;
}

.stat-card {
  padding: 24px;
  border-radius: var(--radius-lg);
  color: white;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.18);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-content .value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-content .label {
  font-size: 14px;
  opacity: 0.9;
}

.today-row {
  margin-bottom: 24px;
}

.today-card {
  background: white;
  padding: 24px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
}

.today-card:hover {
  box-shadow: var(--shadow-md);
}

.today-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.today-icon.borrow {
  background: rgba(64, 158, 255, 0.1);
  color: var(--primary-color);
}

.today-icon.return {
  background: rgba(103, 194, 58, 0.1);
  color: var(--success-color);
}

.today-content .value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.today-content .label {
  font-size: 14px;
  color: var(--text-secondary);
}

.quick-actions {
  background: white;
  padding: 24px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.action-card {
  background: var(--bg-light);
  padding: 24px;
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.action-card:hover {
  background: var(--primary-color);
  color: white;
  transform: translateY(-6px);
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.35);
  border-color: var(--primary-color);
}

.action-icon {
  font-size: 32px;
}

.action-card span {
  font-size: 14px;
  font-weight: 500;
}
</style>
