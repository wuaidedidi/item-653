<template>
  <div class="book-list">
    <div class="page-header">
      <h1 class="page-title">图书管理</h1>
      <el-button v-if="isAdmin" type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>添加图书
      </el-button>
    </div>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索图书名称、作者、ISBN"
        clearable
        style="width: 300px"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="searchCategory" placeholder="选择分类" clearable style="width: 150px">
        <el-option
          v-for="cat in categories"
          :key="cat.id"
          :label="cat.name"
          :value="cat.id"
        />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>
    
    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="books" v-loading="loading" stripe>
        <el-table-column prop="isbn" label="ISBN" width="140" />
        <el-table-column prop="title" label="书名" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push(`/books/${row.id}`)">
              {{ row.title }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="80">
          <template #default="{ row }">
            ¥{{ row.price?.toFixed(2) || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="库存" width="100">
          <template #default="{ row }">
            <el-tag :type="row.available > 0 ? 'success' : 'danger'">
              {{ row.available }}/{{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.available > 0 && row.status === 1" 
              type="primary" 
              size="small"
              @click="handleBorrow(row)"
            >借阅</el-button>
            <el-button 
              v-if="isAdmin" 
              type="warning" 
              size="small"
              @click="openDialog(row)"
            >编辑</el-button>
            <el-button 
              v-if="isAdmin" 
              type="danger" 
              size="small"
              @click="handleDelete(row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="loadBooks"
        />
      </div>
    </div>
    
    <!-- 编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑图书' : '添加图书'"
      width="600px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="form.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="form.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker 
            v-model="form.publishDate" 
            type="date" 
            value-format="YYYY-MM-DD"
            placeholder="选择出版日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入图书简介" 
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          <span style="margin-left: 8px">{{ form.status === 1 ? '上架' : '下架' }}</span>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getBooks, createBook, updateBook, deleteBook } from '@/api/book'
import { getCategories } from '@/api/category'
import { borrowBook } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)

const books = ref([])
const categories = ref([])
const searchKeyword = ref('')
const searchCategory = ref(null)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const formRef = ref(null)
const form = reactive({
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  categoryId: null,
  price: 0,
  stock: 1,
  publishDate: '',
  description: '',
  status: 1
})

const rules = {
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

onMounted(() => {
  loadBooks()
  loadCategories()
})

async function loadBooks() {
  loading.value = true
  try {
    const res = await getBooks({
      keyword: searchKeyword.value || undefined,
      categoryId: searchCategory.value || undefined,
      page: pagination.page,
      size: pagination.size
    })
    books.value = res.data.list
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res.data
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

function handleSearch() {
  pagination.page = 1
  loadBooks()
}

function resetSearch() {
  searchKeyword.value = ''
  searchCategory.value = null
  pagination.page = 1
  loadBooks()
}

function openDialog(book = null) {
  isEdit.value = !!book
  editId.value = book?.id || null
  
  if (book) {
    Object.assign(form, {
      isbn: book.isbn,
      title: book.title,
      author: book.author,
      publisher: book.publisher || '',
      categoryId: book.categoryId,
      price: book.price || 0,
      stock: book.stock || 1,
      publishDate: book.publishDate || '',
      description: book.description || '',
      status: book.status
    })
  } else {
    Object.assign(form, {
      isbn: '',
      title: '',
      author: '',
      publisher: '',
      categoryId: null,
      price: 0,
      stock: 1,
      publishDate: '',
      description: '',
      status: 1
    })
  }
  
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (isEdit.value) {
        await updateBook(editId.value, form)
        ElMessage.success('更新成功')
      } else {
        await createBook(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadBooks()
    } finally {
      submitting.value = false
    }
  })
}

async function handleDelete(book) {
  try {
    await ElMessageBox.confirm(`确定要删除《${book.title}》吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteBook(book.id)
    ElMessage.success('删除成功')
    loadBooks()
  } catch {
    // 取消
  }
}

async function handleBorrow(book) {
  try {
    await ElMessageBox.confirm(`确定要借阅《${book.title}》吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await borrowBook(book.id, 30)
    ElMessage.success('借阅成功')
    loadBooks()
  } catch {
    // 取消
  }
}
</script>

<style scoped>
.book-list {
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
