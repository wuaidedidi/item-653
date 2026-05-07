import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', public: true }
  },
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'books',
        name: 'BookList',
        component: () => import('@/views/BookList.vue'),
        meta: { title: '图书管理', icon: 'Reading' }
      },
      {
        path: 'books/:id',
        name: 'BookDetail',
        component: () => import('@/views/BookDetail.vue'),
        meta: { title: '图书详情', hidden: true }
      },
      {
        path: 'categories',
        name: 'CategoryList',
        component: () => import('@/views/CategoryList.vue'),
        meta: { title: '分类管理', icon: 'Menu', admin: true }
      },
      {
        path: 'borrows',
        name: 'BorrowList',
        component: () => import('@/views/BorrowList.vue'),
        meta: { title: '借阅管理', icon: 'Document' }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/UserList.vue'),
        meta: { title: '用户管理', icon: 'User', admin: true }
      },
      {
        path: 'barrages',
        name: 'BarrageWall',
        component: () => import('@/views/BarrageWall.vue'),
        meta: { title: '弹幕墙', icon: 'ChatDotRound' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', hidden: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || '图书管理系统'} - 图书管理系统`
  
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  const isAdmin = userStore.isAdmin
  
  // 公开页面直接放行
  if (to.meta.public) {
    if (isLoggedIn && (to.path === '/login' || to.path === '/register')) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }
  
  // 需要登录
  if (!isLoggedIn) {
    next('/login')
    return
  }
  
  // 需要管理员权限
  if (to.meta.admin && !isAdmin) {
    next('/dashboard')
    return
  }
  
  next()
})

export default router
