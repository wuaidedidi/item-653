<template>
  <div class="borrow-list">
    <div class="page-header">
      <h1 class="page-title">借阅管理</h1>
    </div>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名或书名"
        clearable
        style="width: 250px"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="searchStatus" placeholder="借阅状态" clearable style="width: 120px">
        <el-option label="借阅中" :value="0" />
        <el-option label="已归还" :value="1" />
        <el-option label="已逾期" :value="2" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>
    
    <div class="table-container">
      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="借阅人" width="120" />
        <el-table-column prop="bookTitle" label="图书" min-width="200" />
        <el-table-column prop="bookIsbn" label="ISBN" width="140" />
        <el-table-column prop="borrowDate" label="借阅时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.borrowDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="应还时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="归还时间" width="160">
          <template #default="{ row }">
            {{ row.returnDate ? formatDate(row.returnDate) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0 || row.status === 2">
              <el-button type="success" size="small" @click="handleReturn(row)">归还</el-button>
              <el-button type="primary" size="small" @click="handleRenew(row)">续借</el-button>
            </template>
            <span v-else class="text-muted">-</span>
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
          @change="loadRecords"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getBorrowRecords, returnBook, renewBook } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const loading = ref(false)
const records = ref([])
const searchKeyword = ref('')
const searchStatus = ref(null)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

onMounted(() => {
  loadRecords()
})

async function loadRecords() {
  loading.value = true
  try {
    const res = await getBorrowRecords({
      keyword: searchKeyword.value || undefined,
      status: searchStatus.value,
      page: pagination.page,
      size: pagination.size
    })
    records.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  loadRecords()
}

function resetSearch() {
  searchKeyword.value = ''
  searchStatus.value = null
  pagination.page = 1
  loadRecords()
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 16)
}

function getStatusType(status) {
  const types = { 0: 'primary', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = { 0: '借阅中', 1: '已归还', 2: '已逾期' }
  return texts[status] || '未知'
}

async function handleReturn(record) {
  try {
    await ElMessageBox.confirm(`确定归还《${record.bookTitle}》吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await returnBook(record.id)
    ElMessage.success('归还成功')
    loadRecords()
  } catch {
    // 取消
  }
}

async function handleRenew(record) {
  try {
    await ElMessageBox.confirm(`确定续借《${record.bookTitle}》30天吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await renewBook(record.id, 30)
    ElMessage.success('续借成功')
    loadRecords()
  } catch {
    // 取消
  }
}
</script>

<style scoped>
.borrow-list {
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

.text-muted {
  color: var(--text-secondary);
}
</style>
