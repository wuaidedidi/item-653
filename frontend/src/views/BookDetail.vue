<template>
  <div class="book-detail" v-loading="loading">
    <div class="page-header">
      <el-button @click="$router.back()" :icon="ArrowLeft">返回</el-button>
    </div>
    
    <div class="detail-content" v-if="book">
      <div class="book-info-card">
        <div class="book-cover">
          <img v-if="book.coverUrl" :src="book.coverUrl" :alt="book.title" class="cover-image" />
          <el-icon v-else class="cover-icon"><Reading /></el-icon>
        </div>
        <div class="book-info">
          <h1 class="book-title">{{ book.title }}</h1>
          <div class="book-meta">
            <span><strong>作者:</strong> {{ book.author }}</span>
            <span><strong>ISBN:</strong> {{ book.isbn }}</span>
            <span><strong>出版社:</strong> {{ book.publisher || '-' }}</span>
            <span><strong>分类:</strong> {{ book.categoryName || '-' }}</span>
            <span><strong>价格:</strong> ¥{{ book.price?.toFixed(2) || '-' }}</span>
            <span><strong>出版日期:</strong> {{ book.publishDate || '-' }}</span>
          </div>
          <div class="book-stock">
            <el-tag :type="book.available > 0 ? 'success' : 'danger'" size="large">
              库存: {{ book.available }}/{{ book.stock }}
            </el-tag>
            <el-tag :type="book.status === 1 ? 'success' : 'info'" size="large">
              {{ book.status === 1 ? '上架中' : '已下架' }}
            </el-tag>
          </div>
          <div class="book-actions">
            <el-button 
              v-if="book.available > 0 && book.status === 1"
              type="primary" 
              size="large"
              @click="handleBorrow"
            >
              <el-icon><Reading /></el-icon>借阅此书
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="book-description" v-if="book.description">
        <h2>图书简介</h2>
        <p>{{ book.description }}</p>
      </div>
      
      <!-- 弹幕区域 -->
      <div class="barrage-section">
        <h2>读者评论弹幕</h2>
        <div class="barrage-container" ref="barrageContainer"></div>

        <!-- 发送弹幕 -->
        <div class="barrage-input">
          <el-input
            v-model="barrageContent"
            placeholder="发一条弹幕评论..."
            maxlength="100"
            show-word-limit
            @keyup.enter="sendBarrage"
          />
          <el-color-picker v-model="barrageColor" size="default" />
          <el-button type="primary" @click="sendBarrage" :loading="sending">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getBookById } from '@/api/book'
import { borrowBook } from '@/api/borrow'
import { getBookBarrages, sendBarrage as sendBarrageApi } from '@/api/barrage'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Reading } from '@element-plus/icons-vue'

const route = useRoute()
const loading = ref(true)
const sending = ref(false)
const book = ref(null)
const barrages = ref([])
const barrageContent = ref('')
const barrageColor = ref('#FF6B6B')
const barrageContainer = ref(null)

let animationTimer = null
let barrageIndex = 0

onMounted(async () => {
  await loadBook()
  await loadBarrages()
  startBarrageAnimation()
})

onUnmounted(() => {
  if (animationTimer) {
    clearInterval(animationTimer)
  }
})

async function loadBook() {
  try {
    const res = await getBookById(route.params.id)
    book.value = res.data
  } finally {
    loading.value = false
  }
}

async function loadBarrages() {
  try {
    const res = await getBookBarrages(route.params.id, 50)
    barrages.value = res.data
  } catch (error) {
    console.error('加载弹幕失败', error)
  }
}

function escapeHtml(str) {
  const div = document.createElement('div')
  div.textContent = str
  return div.innerHTML
}

function shootOneBarrage(raw) {
  const container = barrageContainer.value
  if (!container) return

  const containerWidth = container.offsetWidth

  const el = document.createElement('div')
  el.className = 'barrage-item'
  const color = raw.color || '#FF6B6B'
  const user = raw.nickname || raw.username || '匿名'
  el.innerHTML = `<span class="barrage-content" style="color:${color}">${escapeHtml(raw.content)}</span><span class="barrage-user">- ${escapeHtml(user)}</span>`

  const trackId = Math.floor(Math.random() * 8)
  const top = trackId * 35 + 10
  const speed = 80 + Math.random() * 70

  el.style.top = `${top}px`
  el.style.visibility = 'hidden'
  container.appendChild(el)
  const itemWidth = el.offsetWidth

  let currentX = containerWidth
  const endX = -itemWidth

  el.style.transform = `translateX(${currentX}px)`
  el.style.visibility = ''

  let lastTime = null
  function tick(timestamp) {
    if (!el.parentNode) return
    if (lastTime === null) lastTime = timestamp
    const delta = (timestamp - lastTime) / 1000
    lastTime = timestamp
    currentX -= speed * delta
    el.style.transform = `translateX(${currentX}px)`
    if (currentX <= endX) {
      el.remove()
      return
    }
    requestAnimationFrame(tick)
  }
  requestAnimationFrame(tick)
}

function startBarrageAnimation() {
  if (animationTimer) clearInterval(animationTimer)
  animationTimer = setInterval(() => {
    if (barrages.value.length === 0 || !barrageContainer.value) return
    const raw = barrages.value[barrageIndex]
    barrageIndex = (barrageIndex + 1) % barrages.value.length
    shootOneBarrage(raw)
  }, 1500)
}

async function handleBorrow() {
  try {
    await ElMessageBox.confirm(`确定要借阅《${book.value.title}》吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await borrowBook(book.value.id, 30)
    ElMessage.success('借阅成功')
    await loadBook()
  } catch {
    // 取消
  }
}

async function sendBarrage() {
  if (!barrageContent.value.trim()) {
    ElMessage.warning('请输入弹幕内容')
    return
  }
  
  sending.value = true
  try {
    await sendBarrageApi({
      bookId: book.value.id,
      content: barrageContent.value,
      color: barrageColor.value
    })
    ElMessage.success('弹幕发送成功')
    barrageContent.value = ''
    await loadBarrages()
  } finally {
    sending.value = false
  }
}
</script>

<style scoped>
.book-detail {
  animation: fadeIn 0.5s ease;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.book-info-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 32px;
  display: flex;
  gap: 32px;
  box-shadow: var(--shadow-sm);
}

.book-cover {
  width: 200px;
  height: 280px;
  background: var(--gradient-primary);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-icon {
  font-size: 80px;
  color: white;
  opacity: 0.5;
}

.book-info {
  flex: 1;
}

.book-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.book-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
  color: var(--text-regular);
}

.book-meta strong {
  color: var(--text-secondary);
  margin-right: 8px;
}

.book-stock {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.book-actions {
  display: flex;
  gap: 12px;
}

.book-description {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.book-description h2 {
  font-size: 18px;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.book-description p {
  color: var(--text-regular);
  line-height: 1.8;
}

.barrage-section {
  background: white;
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.barrage-section h2 {
  font-size: 18px;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.barrage-container {
  height: 300px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: var(--radius-md);
  position: relative;
  overflow: hidden;
  margin-bottom: 16px;
}

.barrage-input {
  display: flex;
  gap: 12px;
  align-items: center;
}

.barrage-input .el-input {
  flex: 1;
}

@media (max-width: 768px) {
  .book-info-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .book-meta {
    grid-template-columns: 1fr;
  }
  
  .book-stock, .book-actions {
    justify-content: center;
  }
}
</style>

<!-- 弹幕元素由 JS 创建，不受 scoped 限制 -->
<style>
.barrage-container .barrage-item {
  position: absolute;
  left: 0;
  white-space: nowrap;
  user-select: none;
  pointer-events: none;
  will-change: transform;
  display: flex;
  align-items: center;
  gap: 8px;
}

.barrage-container .barrage-content {
  font-size: 16px;
  font-weight: 500;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
}

.barrage-container .barrage-user {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}
</style>
