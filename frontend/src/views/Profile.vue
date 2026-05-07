<template>
  <div class="profile">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
    </div>
    
    <el-row :gutter="24">
      <!-- 个人信息卡片 -->
      <el-col :xs="24" :md="12">
        <div class="profile-card">
          <h2>个人信息</h2>
          <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="80px">
            <el-form-item label="用户名">
              <el-input :value="user?.username" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="infoForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="infoLoading" @click="updateInfo">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
      
      <!-- 修改密码卡片 -->
      <el-col :xs="24" :md="12">
        <div class="profile-card">
          <h2>修改密码</h2>
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input 
                v-model="pwdForm.oldPassword" 
                type="password" 
                show-password
                placeholder="请输入原密码" 
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="pwdForm.newPassword" 
                type="password" 
                show-password
                placeholder="请输入新密码" 
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input 
                v-model="pwdForm.confirmPassword" 
                type="password" 
                show-password
                placeholder="请再次输入新密码" 
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="pwdLoading" @click="changePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
    
    <!-- 账号信息 -->
    <div class="account-info">
      <h2>账号信息</h2>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ user?.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ user?.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="user?.role === 1 ? 'danger' : 'primary'">
            {{ user?.role === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="user?.status === 1 ? 'success' : 'info'">
            {{ user?.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ formatDate(user?.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ formatDate(user?.updatedAt) }}
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const user = computed(() => userStore.user)

const infoFormRef = ref(null)
const pwdFormRef = ref(null)
const infoLoading = ref(false)
const pwdLoading = ref(false)

const infoForm = reactive({
  nickname: '',
  email: '',
  phone: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const infoRules = {
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^$|^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

onMounted(() => {
  loadUserInfo()
})

function loadUserInfo() {
  if (user.value) {
    infoForm.nickname = user.value.nickname || ''
    infoForm.email = user.value.email || ''
    infoForm.phone = user.value.phone || ''
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

async function updateInfo() {
  if (!infoFormRef.value) return
  
  await infoFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    infoLoading.value = true
    try {
      await userStore.updateUser(infoForm)
      ElMessage.success('信息更新成功')
    } finally {
      infoLoading.value = false
    }
  })
}

async function changePassword() {
  if (!pwdFormRef.value) return
  
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    pwdLoading.value = true
    try {
      await userStore.updatePassword({
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('密码修改成功')
      // 清空表单
      pwdForm.oldPassword = ''
      pwdForm.newPassword = ''
      pwdForm.confirmPassword = ''
    } finally {
      pwdLoading.value = false
    }
  })
}
</script>

<style scoped>
.profile {
  animation: fadeIn 0.5s ease;
}

.profile-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 24px;
}

.profile-card h2 {
  font-size: 18px;
  margin-bottom: 20px;
  color: var(--text-primary);
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-lighter);
}

.account-info {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.account-info h2 {
  font-size: 18px;
  margin-bottom: 20px;
  color: var(--text-primary);
}
</style>
