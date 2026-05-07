<template>
  <div class="user-list">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
    </div>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名、昵称、邮箱"
        clearable
        style="width: 300px"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="searchRole" placeholder="角色" clearable style="width: 120px">
        <el-option label="普通用户" :value="0" />
        <el-option label="管理员" :value="1" />
      </el-select>
      <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 120px">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>
    
    <div class="table-container">
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'danger' : 'primary'">
              {{ row.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button 
              v-if="!isCurrentUser(row.id)"
              type="danger" 
              size="small" 
              @click="handleDelete(row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="loadUsers"
        />
      </div>
    </div>
    
    <!-- 编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="编辑用户"
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名">
          <el-input :value="editUser?.username" disabled />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" v-if="!isCurrentUser(editUser?.id)">
          <el-radio-group v-model="form.role">
            <el-radio :label="0">普通用户</el-radio>
            <el-radio :label="1">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" v-if="!isCurrentUser(editUser?.id)">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          <span style="margin-left: 8px">{{ form.status === 1 ? '启用' : '禁用' }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getUsers, updateUser, deleteUser } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const editUser = ref(null)

const users = ref([])
const searchKeyword = ref('')
const searchRole = ref(null)
const searchStatus = ref(null)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const formRef = ref(null)
const form = reactive({
  nickname: '',
  email: '',
  phone: '',
  role: 0,
  status: 1
})

const rules = {
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^$|^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadUsers()
})

function isCurrentUser(id) {
  return userStore.user?.id === id
}

async function loadUsers() {
  loading.value = true
  try {
    const res = await getUsers({
      keyword: searchKeyword.value || undefined,
      role: searchRole.value,
      status: searchStatus.value,
      page: pagination.page,
      size: pagination.size
    })
    users.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  loadUsers()
}

function resetSearch() {
  searchKeyword.value = ''
  searchRole.value = null
  searchStatus.value = null
  pagination.page = 1
  loadUsers()
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

function openDialog(user) {
  editUser.value = user
  Object.assign(form, {
    nickname: user.nickname || '',
    email: user.email || '',
    phone: user.phone || '',
    role: user.role,
    status: user.status
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const data = { ...form }
      // 不能修改自己的角色和状态
      if (isCurrentUser(editUser.value?.id)) {
        delete data.role
        delete data.status
      }
      
      await updateUser(editUser.value.id, data)
      ElMessage.success('更新成功')
      dialogVisible.value = false
      loadUsers()
    } finally {
      submitting.value = false
    }
  })
}

async function handleDelete(user) {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${user.username}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteUser(user.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch {
    // 取消
  }
}
</script>

<style scoped>
.user-list {
  animation: fadeIn 0.5s ease;
}

.table-container {
  background: white;
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
