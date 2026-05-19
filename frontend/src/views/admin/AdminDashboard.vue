<template>
  <div class="admin-container">
    <!-- 移动端顶部栏 -->
    <header class="mobile-header">
      <el-button class="mobile-menu-btn" @click="mobileMenuVisible = true">
        <el-icon size="24"><Menu /></el-icon>
      </el-button>
      <div class="mobile-title">
        <span class="brand-name">CapyGlide</span>
        <span class="brand-tag">管理后台</span>
      </div>
      <el-avatar :size="32" class="mobile-avatar">{{ adminName?.charAt(0) || 'A' }}</el-avatar>
    </header>

    <!-- 移动端抽屉菜单 -->
    <el-drawer v-model="mobileMenuVisible" direction="ltr" size="280px" :with-header="false" class="mobile-drawer">
      <div class="drawer-content">
        <div class="drawer-header">
          <div class="drawer-logo">
            <div class="logo-icon">
              <svg viewBox="0 0 64 64" fill="none">
                <circle cx="14" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
                <circle cx="50" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
                <path d="M14 50L24 30H40L50 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                <path d="M24 30L30 20H38" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
                <rect x="28" y="18" width="12" height="4" rx="1" fill="currentColor"/>
              </svg>
            </div>
            <div class="logo-text">
              <span class="brand-name">CapyGlide</span>
              <span class="brand-tag">管理后台</span>
            </div>
          </div>
        </div>

        <nav class="drawer-nav">
          <div class="nav-section">
            <span class="nav-label">数据中心</span>
            <a class="nav-item" :class="{ active: activeTab === 'overview' }" @click="switchTab('overview'); mobileMenuVisible = false">
              <el-icon><DataLine /></el-icon>
              <span>运营概览</span>
            </a>
            <a class="nav-item" :class="{ active: activeTab === 'reports' }" @click="switchTab('reports'); mobileMenuVisible = false">
              <el-icon><TrendCharts /></el-icon>
              <span>收入报表</span>
            </a>
            <a class="nav-item" :class="{ active: activeTab === 'userAnalysis' }" @click="switchTab('userAnalysis'); mobileMenuVisible = false">
              <el-icon><User /></el-icon>
              <span>用户分析</span>
            </a>
            <a class="nav-item" :class="{ active: activeTab === 'orderAnalysis' }" @click="switchTab('orderAnalysis'); mobileMenuVisible = false">
              <el-icon><Tickets /></el-icon>
              <span>订单分析</span>
            </a>
          </div>

          <div class="nav-section">
            <span class="nav-label">用户与车辆</span>
            <a class="nav-item" :class="{ active: activeTab === 'users' }" @click="switchTab('users'); mobileMenuVisible = false">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </a>
            <a class="nav-item" :class="{ active: activeTab === 'scooters' }" @click="switchTab('scooters'); mobileMenuVisible = false">
              <el-icon><Van /></el-icon>
              <span>车辆管理</span>
            </a>
            <a class="nav-item" :class="{ active: activeTab === 'map' }" @click="switchTab('map'); mobileMenuVisible = false">
              <el-icon><MapLocation /></el-icon>
              <span>车辆地图</span>
            </a>
            <a class="nav-item" :class="{ active: activeTab === 'depot' }" @click="switchTab('depot'); mobileMenuVisible = false">
              <el-icon><LocationFilled /></el-icon>
              <span>服务点管理</span>
            </a>
          </div>

          <div class="nav-section">
            <span class="nav-label">运营支持</span>
            <a class="nav-item" :class="{ active: activeTab === 'booking' }" @click="switchTab('booking'); mobileMenuVisible = false">
              <el-icon><Tickets /></el-icon>
              <span>代客预订</span>
            </a>
            <a class="nav-item" :class="{ active: activeTab === 'pricing' }" @click="switchTab('pricing'); mobileMenuVisible = false">
              <el-icon><Money /></el-icon>
              <span>价格配置</span>
            </a>
            <a class="nav-item" :class="{ active: activeTab === 'discount' }" @click="switchTab('discount'); mobileMenuVisible = false">
              <el-icon><Sell /></el-icon>
              <span>折扣管理</span>
            </a>
          </div>

          <div class="nav-section">
            <span class="nav-label">问题处理</span>
            <a class="nav-item" :class="{ active: activeTab === 'issues' }" @click="switchTab('issues'); mobileMenuVisible = false">
              <el-icon><Warning /></el-icon>
              <span>故障工单</span>
              <span v-if="pendingIssues > 0" class="badge">{{ pendingIssues }}</span>
            </a>
            <a class="nav-item" :class="{ active: activeTab === 'feedback' }" @click="switchTab('feedback'); mobileMenuVisible = false">
              <el-icon><ChatDotRound /></el-icon>
              <span>用户反馈</span>
            </a>
          </div>
        </nav>

        <div class="drawer-footer">
          <div class="admin-info">
            <el-avatar :size="36" class="admin-avatar">{{ adminName?.charAt(0) || 'A' }}</el-avatar>
            <div class="admin-detail">
              <span class="admin-name">{{ adminName }}</span>
              <span class="admin-role">系统管理员</span>
            </div>
          </div>
          <el-button type="danger" plain size="small" @click="logout" class="logout-btn">
            <el-icon><SwitchButton /></el-icon>
          </el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="sidebar-logo">
          <div class="logo-icon">
            <svg viewBox="0 0 64 64" fill="none">
              <circle cx="14" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
              <circle cx="50" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
              <path d="M14 50L24 30H40L50 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              <path d="M24 30L30 20H38" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              <rect x="28" y="18" width="12" height="4" rx="1" fill="currentColor"/>
            </svg>
          </div>
          <div class="logo-text">
            <span class="brand-name">CapyGlide</span>
            <span class="brand-tag">管理后台</span>
          </div>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-section">
          <span class="nav-label">数据中心</span>
          <a class="nav-item" :class="{ active: activeTab === 'overview' }" @click="switchTab('overview')">
            <el-icon><DataLine /></el-icon>
            <span>运营概览</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'reports' }" @click="switchTab('reports')">
            <el-icon><TrendCharts /></el-icon>
            <span>收入报表</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'userAnalysis' }" @click="switchTab('userAnalysis')">
            <el-icon><User /></el-icon>
            <span>用户分析</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'orderAnalysis' }" @click="switchTab('orderAnalysis')">
            <el-icon><Tickets /></el-icon>
            <span>订单分析</span>
          </a>
        </div>

        <div class="nav-section">
          <span class="nav-label">用户与车辆</span>
          <a class="nav-item" :class="{ active: activeTab === 'users' }" @click="switchTab('users')">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'scooters' }" @click="switchTab('scooters')">
            <el-icon><Van /></el-icon>
            <span>车辆管理</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'map' }" @click="switchTab('map')">
            <el-icon><MapLocation /></el-icon>
            <span>车辆地图</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'depot' }" @click="switchTab('depot')">
            <el-icon><LocationFilled /></el-icon>
            <span>服务点管理</span>
          </a>
        </div>

        <div class="nav-section">
          <span class="nav-label">运营支持</span>
          <a class="nav-item" :class="{ active: activeTab === 'booking' }" @click="switchTab('booking')">
            <el-icon><Tickets /></el-icon>
            <span>代客预订</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'pricing' }" @click="switchTab('pricing')">
            <el-icon><Money /></el-icon>
            <span>价格配置</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'discount' }" @click="switchTab('discount')">
            <el-icon><Sell /></el-icon>
            <span>折扣管理</span>
          </a>
        </div>

        <div class="nav-section">
          <span class="nav-label">问题处理</span>
          <a class="nav-item" :class="{ active: activeTab === 'issues' }" @click="switchTab('issues')">
            <el-icon><Warning /></el-icon>
            <span>故障工单</span>
            <span v-if="pendingIssues > 0" class="badge">{{ pendingIssues }}</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'feedback' }" @click="switchTab('feedback')">
            <el-icon><ChatDotRound /></el-icon>
            <span>用户反馈</span>
          </a>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="admin-info">
          <el-avatar :size="36" class="admin-avatar">{{ adminName?.charAt(0) || 'A' }}</el-avatar>
          <div class="admin-detail">
            <span class="admin-name">{{ adminName }}</span>
            <span class="admin-role">系统管理员</span>
          </div>
        </div>
        <el-button type="danger" plain size="small" @click="logout" class="logout-btn">
          <el-icon><SwitchButton /></el-icon>
        </el-button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main-area">
      <!-- 内容区域 -->
      <div class="content-wrapper">
        <!-- 概览 -->
        <div v-if="activeTab === 'overview'" class="tab-content">
          <!-- 核心统计 -->
          <div class="stats-grid">
            <div class="stat-card primary">
              <div class="stat-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">¥{{ totalIncome }}</span>
                <span class="stat-label">总收入</span>
              </div>
              <div class="stat-trend up">
                <el-icon><Top /></el-icon>
                <span>+12.5%</span>
              </div>
            </div>
            <div class="stat-card success">
              <div class="stat-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ totalUsers }}</span>
                <span class="stat-label">注册用户</span>
              </div>
              <div class="stat-sub">
                <span>今日+{{ todayNewUsers }}</span>
                <span>本周+{{ weekNewUsers }}</span>
              </div>
            </div>
            <div class="stat-card warning">
              <div class="stat-icon">
                <el-icon><Van /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ activeBookings }}</span>
                <span class="stat-label">进行中行程</span>
              </div>
              <div class="stat-sub">
                <span>使用率 {{ usageRate }}%</span>
              </div>
            </div>
            <div class="stat-card info">
              <div class="stat-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ pendingIssues }}</span>
                <span class="stat-label">待处理工单</span>
              </div>
            </div>
          </div>

          <!-- 运营指标 -->
          <div class="overview-row">
            <div class="chart-card">
              <div class="card-header">
                <h3>运营指标</h3>
              </div>
              <div class="metrics-grid">
                <div class="metric-item">
                  <span class="metric-label">今日收入</span>
                  <span class="metric-value">¥{{ todayIncome }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">本周收入</span>
                  <span class="metric-value">¥{{ weeklyIncome }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">可用车辆</span>
                  <span class="metric-value">{{ availableCount }}/{{ scooters.length }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">服务点</span>
                  <span class="metric-value">{{ depots.length }} 个</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">平均骑行时长</span>
                  <span class="metric-value">{{ avgRideDuration }} 分钟</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">订单完成率</span>
                  <span class="metric-value">{{ completionRate }}%</span>
                </div>
              </div>
            </div>
            <div class="chart-card">
              <div class="card-header">
                <h3>各租期收入分布</h3>
              </div>
              <div ref="chartByOptionRef" class="chart"></div>
            </div>
          </div>

          <!-- 每日趋势 & 车辆预警 -->
          <div class="overview-row">
            <div class="chart-card">
              <div class="card-header">
                <h3>每日收入趋势</h3>
              </div>
              <div ref="chartDailyRef" class="chart"></div>
            </div>
            <div class="chart-card">
              <div class="card-header">
                <h3>车辆健康预警</h3>
              </div>
              <div class="alert-list">
                <div v-if="lowBatteryScooters.length === 0" class="no-alert">暂无预警</div>
                <div v-for="scooter in lowBatteryScooters.slice(0, 5)" :key="scooter.id" class="alert-item danger">
                  <el-icon><Warning /></el-icon>
                  <span>{{ scooter.scooterNumber }} 电量过低 ({{ scooter.batteryLevel }}%)</span>
                </div>
                <div v-for="depot in lowStockDepots" :key="depot.id" class="alert-item warning">
                  <el-icon><Warning /></el-icon>
                  <span>{{ depot.name }} 库存不足 ({{ depot.currentStock }}/{{ depot.capacity }})</span>
                </div>
                <div v-for="depot in overflowDepots" :key="'overflow-' + depot.id" class="alert-item danger">
                  <el-icon><WarningFilled /></el-icon>
                  <span>{{ depot.name }} 库存溢出 ({{ depot.currentStock }}/{{ depot.capacity }})</span>
                </div>
                <div v-for="scooter in maintenanceNeeded.slice(0, 3)" :key="scooter.id" class="alert-item info">
                  <el-icon><Tools /></el-icon>
                  <span>{{ scooter.scooterNumber }} 需要维护</span>
                </div>
              </div>
            </div>
          </div>

          <div class="quick-actions">
            <div class="card-header">
              <h3>快捷操作</h3>
            </div>
            <div class="actions-grid">
              <div class="action-card" @click="switchTab('booking')">
                <div class="action-icon">
                  <el-icon><Tickets /></el-icon>
                </div>
                <span>代客预订</span>
              </div>
              <div class="action-card" @click="switchTab('scooters')">
                <div class="action-icon">
                  <el-icon><Van /></el-icon>
                </div>
                <span>车辆管理</span>
              </div>
              <div class="action-card" @click="switchTab('map')">
                <div class="action-icon">
                  <el-icon><MapLocation /></el-icon>
                </div>
                <span>车辆地图</span>
              </div>
              <div class="action-card" @click="switchTab('issues')">
                <div class="action-icon">
                  <el-icon><Warning /></el-icon>
                </div>
                <span>故障工单</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 收入报表 -->
        <div v-if="activeTab === 'reports'" class="tab-content">
          <div class="stats-grid">
            <div class="stat-card primary">
              <div class="stat-icon"><el-icon><Money /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">¥{{ totalIncome }}</span>
                <span class="stat-label">总收入</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon"><el-icon><Calendar /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">¥{{ weeklyIncome }}</span>
                <span class="stat-label">本周收入</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon"><el-icon><Clock /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">¥{{ todayIncome }}</span>
                <span class="stat-label">今日收入</span>
              </div>
            </div>
          </div>
          <div class="chart-row">
            <div class="chart-card">
              <div class="card-header"><h3>各租期收入分布</h3></div>
              <div ref="chartByOptionRef2" class="chart"></div>
            </div>
            <div class="chart-card">
              <div class="card-header"><h3>每日收入趋势</h3></div>
              <div ref="chartDailyRef2" class="chart"></div>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeTab === 'users'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>用户列表</h3>
              <div class="header-actions">
                <el-input v-model="userSearch" placeholder="搜索用户..." clearable style="width: 240px">
                  <template #prefix><el-icon><Search /></el-icon></template>
                </el-input>
              </div>
            </div>
            <el-table :data="filteredUsers" stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="username" label="用户名" min-width="140" />
              <el-table-column prop="email" label="邮箱" min-width="200" />
              <el-table-column prop="phone" label="电话" width="150" />
              <el-table-column prop="role" label="角色" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">{{ row.role }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="isActive" label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.isActive ? 'success' : 'info'" size="small">{{ row.isActive ? '活跃' : '禁用' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" :type="row.isActive ? 'danger' : 'success'" plain @click="toggleUserStatus(row)">
                    {{ row.isActive ? '禁用' : '启用' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 车辆管理 -->
        <div v-if="activeTab === 'scooters'" class="tab-content">
          <div class="stats-grid mini">
            <div class="stat-mini available">
              <span class="mini-value">{{ availableCount }}</span>
              <span class="mini-label">可用</span>
            </div>
            <div class="stat-mini inuse">
              <span class="mini-value">{{ inUseCount }}</span>
              <span class="mini-label">使用中</span>
            </div>
            <div class="stat-mini maintenance">
              <span class="mini-value">{{ maintenanceCount }}</span>
              <span class="mini-label">维护中</span>
            </div>
            <div class="stat-mini total">
              <span class="mini-value">{{ scooters.length }}</span>
              <span class="mini-label">车辆总数</span>
            </div>
          </div>
          <div class="content-card">
            <div class="card-header">
              <h3>车辆状态管理</h3>
              <div class="header-actions">
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <el-table :data="scooters" stripe v-loading="loading">
              <el-table-column prop="scooterNumber" label="编号" width="130" />
              <el-table-column prop="status" label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="batteryLevel" label="电量" width="100">
                <template #default="{ row }">
                  <div class="battery-cell">
                    <span class="battery-val">{{ row.batteryLevel || '—' }}%</span>
                    <div class="battery-bar">
                      <div class="battery-fill" :class="getBatteryClass(row.batteryLevel)" :style="{ width: (row.batteryLevel || 0) + '%' }"></div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="location" label="位置" min-width="180" />
              <el-table-column prop="depotId" label="服务点" width="130">
                <template #default="{ row }">{{ getDepotName(row.depotId) }}</template>
              </el-table-column>
              <el-table-column label="操作" width="280" fixed="right">
                <template #default="{ row }">
                  <div class="action-buttons">
                    <el-button size="small" type="success" plain @click="setStatus(row.id, 'AVAILABLE')">可用</el-button>
                    <el-button size="small" type="warning" plain @click="setStatus(row.id, 'IN_USE')">使用中</el-button>
                    <el-button size="small" type="info" plain @click="setStatus(row.id, 'MAINTENANCE')">维护</el-button>
                    <el-button size="small" type="primary" plain @click="openBatteryDialog(row)">⚡电量</el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 服务点管理 -->
        <div v-if="activeTab === 'depot'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>服务点列表</h3>
              <div class="header-actions">
                <el-button type="primary" @click="openAddDepotDialog">
                  <el-icon><Plus /></el-icon>新增服务点
                </el-button>
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <el-table :data="depots" stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="depotNumber" label="编号" width="100" />
              <el-table-column prop="name" label="名称" min-width="160" />
              <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
              <el-table-column prop="latitude" label="纬度" width="100" />
              <el-table-column prop="longitude" label="经度" width="100" />
              <el-table-column prop="capacity" label="容量" width="80" />
              <el-table-column prop="currentStock" label="当前库存" width="110">
                <template #default="{ row }">
                  <span class="stock-badge" :class="getStockClass(row.currentStock, row.capacity)">
                    {{ row.currentStock }}/{{ row.capacity }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">
                    {{ row.status === 'ACTIVE' ? '正常' : '停用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="140" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" type="primary" plain @click="openEditDepotDialog(row)">编辑</el-button>
                  <el-button size="small" type="danger" plain @click="handleDeleteDepot(row)" :disabled="row.totalScooters > 0">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 代客预订 -->
        <div v-if="activeTab === 'booking'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>员工代客预订</h3>
            </div>
            <el-form :model="staffForm" label-width="100px" class="staff-form">
              <!-- ID7: 预订类型切换 -->
              <el-form-item label="预订类型">
                <el-radio-group v-model="staffForm.bookingType" @change="onBookingTypeChange">
                  <el-radio value="REGISTERED">已注册用户</el-radio>
                  <el-radio value="GUEST">未注册用户</el-radio>
                </el-radio-group>
              </el-form-item>

              <!-- 已注册用户：选择用户 -->
              <el-form-item v-if="staffForm.bookingType === 'REGISTERED'" label="选择用户" required>
                <el-select v-model="staffForm.userId" filterable placeholder="搜索并选择用户" style="width: 100%">
                  <el-option v-for="u in users" :key="u.id" :label="`${u.username} (${u.email || '无邮箱'})`" :value="u.id" />
                </el-select>
              </el-form-item>

              <!-- 未注册用户：填写信息 -->
              <template v-if="staffForm.bookingType === 'GUEST'">
                <el-form-item label="姓名" required>
                  <el-input v-model="staffForm.guestName" placeholder="请输入访客姓名" />
                </el-form-item>
                <el-form-item label="电话" required>
                  <el-input v-model="staffForm.guestPhone" placeholder="请输入联系电话" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="staffForm.guestEmail" placeholder="请输入邮箱（可选）" />
                </el-form-item>
              </template>

              <el-form-item label="选择车辆" required>
                <div class="scooter-status-note">
                  <el-icon><InfoFilled /></el-icon>
                  实时更新：每15秒自动刷新可用车辆列表
                </div>
                <el-select v-model="staffForm.scooterId" filterable placeholder="请选择可用车辆" style="width: 100%">
                  <el-option-group v-for="group in scooterOptions" :key="group.label" :label="group.label">
                    <el-option
                      v-for="s in group.options"
                      :key="s.id"
                      :label="`${s.scooterNumber} ${s.status === 'AVAILABLE' ? '✓' : '✗'} ${s.batteryLevel !== null ? '🔋' + s.batteryLevel + '%' : ''}`"
                      :value="s.id"
                      :disabled="s.status !== 'AVAILABLE'"
                    >
                      <div class="scooter-option">
                        <span class="scooter-name">{{ s.scooterNumber }}</span>
                        <span class="scooter-status" :class="s.status.toLowerCase().replace('_', '-')">
                          {{ getStatusText(s.status) }}
                        </span>
                        <span v-if="s.batteryLevel !== null" class="scooter-battery">
                          🔋 {{ s.batteryLevel }}%
                        </span>
                      </div>
                    </el-option>
                  </el-option-group>
                </el-select>
                <div v-if="staffForm.scooterId" class="selected-scooter-info">
                  已选: {{ getSelectedScooterInfo() }}
                </div>
              </el-form-item>
              <el-form-item label="租用时长" required>
                <el-select v-model="staffForm.hireOption" style="width: 100%">
                  <el-option label="1 小时" value="1hr" />
                  <el-option label="4 小时" value="4hr" />
                  <el-option label="1 天" value="1day" />
                  <el-option label="1 周" value="1week" />
                </el-select>
              </el-form-item>
              <el-form-item label="开始时间" required>
                <el-date-picker v-model="staffForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="large" :loading="staffLoading" @click="submitStaffBooking">创建预订订单</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 价格配置 -->
        <div v-if="activeTab === 'pricing'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>价格方案</h3>
            </div>
            <el-table :data="pricingList" stripe>
              <el-table-column prop="hireOption" label="租期代码" width="130" />
              <el-table-column prop="description" label="说明" min-width="200" />
              <el-table-column prop="price" label="价格 (¥)" width="160">
                <template #default="{ row }">
                  <el-input-number v-model="row.price" :min="0" :precision="2" :step="1" size="small" controls-position="right" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="savePricing(row)">保存</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="content-card">
            <div class="card-header">
              <h3>租用选项</h3>
            </div>
            <el-table :data="hireOptions" stripe>
              <el-table-column prop="code" label="代码" width="110" />
              <el-table-column prop="label" label="名称" min-width="180" />
              <el-table-column prop="durationMinutes" label="时长(分钟)" width="130" />
              <el-table-column prop="price" label="价格" width="160">
                <template #default="{ row }">
                  <el-input-number v-model="row.price" :min="0" :precision="2" :step="1" size="small" controls-position="right" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="saveHireOption(row)">保存</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <!-- 超时费用配置 -->
          <div class="content-card overtime-card">
            <div class="card-header">
              <h3>超时费用配置</h3>
              <p class="card-tip">超过租赁时长后，每超时一定时间收取的费用</p>
            </div>
            <el-table :data="overtimeFees" stripe v-loading="overtimeLoading">
              <el-table-column prop="hireOption" label="租期" width="100" />
              <el-table-column prop="hireOptionName" label="说明" min-width="140" />
              <el-table-column prop="feeType" label="收费方式" width="110" align="center">
                <template #default="{ row }">
                  <el-tag size="small" :type="row.feeType === 'HOURLY' ? 'primary' : 'warning'">
                    {{ row.feeType === 'HOURLY' ? '按小时' : '固定' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="超时费率" width="140" align="center">
                <template #default="{ row }">
                  <span class="overtime-fee-text">
                    ¥{{ Number(row.fee).toFixed(2) }}{{ row.feeType === 'HOURLY' ? '/小时' : '/次' }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="maxOvertimeMinutes" label="最大超时" width="100" align="center">
                <template #default="{ row }">
                  {{ row.maxOvertimeMinutes ? (row.maxOvertimeMinutes + '分钟') : '不限' }}
                </template>
              </el-table-column>
              <el-table-column prop="enabled" label="状态" width="80" align="center">
                <template #default="{ row }">
                  <el-switch v-model="row.enabled" @change="toggleOvertimeEnabled(row)" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="editOvertimeFee(row)">编辑</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 超时费用编辑弹窗 -->
        <el-dialog v-model="overtimeDialogVisible" title="编辑超时费用" width="450px">
          <el-form :model="overtimeForm" label-width="110px">
            <el-form-item label="租期">
              <el-input v-model="overtimeForm.hireOption" disabled />
            </el-form-item>
            <el-form-item label="收费方式">
              <el-radio-group v-model="overtimeForm.feeType">
                <el-radio value="HOURLY">按小时</el-radio>
                <el-radio value="FIXED">固定金额</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item :label="overtimeForm.feeType === 'HOURLY' ? '每小时费用' : '固定费用'">
              <el-input-number v-model="overtimeForm.fee" :min="0" :precision="2" :step="0.5" controls-position="right" style="width: 100%" />
            </el-form-item>
            <el-form-item label="最大超时(分钟)">
              <el-input-number v-model="overtimeForm.maxOvertimeMinutes" :min="0" :step="30" controls-position="right" style="width: 100%" />
              <span style="font-size: 12px; color: #909399; margin-left: 8px">0=不限制</span>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="overtimeDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveOvertimeFee" :loading="saving">保存</el-button>
          </template>
        </el-dialog>

        <!-- 折扣管理 -->
        <div v-if="activeTab === 'discount'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>折扣列表</h3>
              <div class="header-actions">
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <el-table :data="discounts" stripe v-loading="loading">
              <el-table-column prop="code" label="折扣码" width="140" />
              <el-table-column prop="description" label="描述" min-width="220" />
              <el-table-column prop="discountType" label="类型" width="110">
                <template #default="{ row }">
                  <el-tag size="small">{{ row.discountType === 'PERCENTAGE' ? '百分比' : '固定金额' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="discountValue" label="优惠值" width="100">
                <template #default="{ row }">
                  {{ row.discountType === 'PERCENTAGE' ? row.discountValue + '%' : '¥' + row.discountValue }}
                </template>
              </el-table-column>
              <el-table-column prop="minBookingValue" label="最低消费" width="110">
                <template #default="{ row }">¥{{ row.minBookingValue || 0 }}</template>
              </el-table-column>
              <el-table-column prop="usageLimit" label="限制次数" width="100" />
              <el-table-column prop="usageCount" label="已使用" width="90" />
              <el-table-column prop="isActive" label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.isActive ? 'success' : 'info'" size="small">{{ row.isActive ? '启用' : '禁用' }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 车辆地图 -->
        <div v-if="activeTab === 'map'" class="tab-content">
          <div class="stats-grid mini">
            <div class="stat-mini available">
              <span class="mini-value">{{ availableCount }}</span>
              <span class="mini-label">可用</span>
            </div>
            <div class="stat-mini inuse">
              <span class="mini-value">{{ inUseCount }}</span>
              <span class="mini-label">使用中</span>
            </div>
            <div class="stat-mini maintenance">
              <span class="mini-value">{{ maintenanceCount }}</span>
              <span class="mini-label">维护中</span>
            </div>
          </div>
          <div class="content-card map-card">
            <div class="card-header">
              <h3>车辆位置地图</h3>
              <div class="header-actions">
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <div class="map-container">
              <div id="adminMap" class="admin-map"></div>
              <div class="map-legend">
                <div class="legend-item">
                  <span class="legend-dot available"></span>
                  <span>可用</span>
                </div>
                <div class="legend-item">
                  <span class="legend-dot inuse"></span>
                  <span>使用中</span>
                </div>
                <div class="legend-item">
                  <span class="legend-dot maintenance"></span>
                  <span>维护中</span>
                </div>
              </div>
              <div class="map-stats">
                <div class="map-stat">
                  <span class="map-stat-value">{{ availableCount }}</span>
                  <span class="map-stat-label">可用车辆</span>
                </div>
                <div class="map-stat">
                  <span class="map-stat-value">{{ inUseCount }}</span>
                  <span class="map-stat-label">使用中</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 故障工单 -->
        <div v-if="activeTab === 'issues'" class="tab-content">
          <div class="stats-grid mini">
            <div class="stat-mini open">
              <span class="mini-value">{{ openIssues }}</span>
              <span class="mini-label">待处理</span>
            </div>
            <div class="stat-mini progress">
              <span class="mini-value">{{ inProgressIssues }}</span>
              <span class="mini-label">处理中</span>
            </div>
            <div class="stat-mini resolved">
              <span class="mini-value">{{ resolvedIssues }}</span>
              <span class="mini-label">已解决</span>
            </div>
            <!-- ID14: 高优先级工单统计 -->
            <div class="stat-mini high-priority">
              <span class="mini-value">{{ highPriorityIssues }}</span>
              <span class="mini-label">高优先级</span>
            </div>
          </div>
          <div class="content-card">
            <div class="card-header">
              <h3>车辆故障报告</h3>
              <div class="header-actions">
                <!-- ID14: 优先级筛选 -->
                <el-select v-model="issuePriorityFilter" placeholder="全部优先级" clearable style="width: 140px; margin-right: 8px;">
                  <el-option label="全部" value="" />
                  <el-option label="高优先级" value="HIGH" />
                  <el-option label="中优先级" value="MEDIUM" />
                  <el-option label="普通" value="NORMAL" />
                  <el-option label="低优先级" value="LOW" />
                </el-select>
                <el-button @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <!-- ID14: 高优先级问题快速查看 -->
            <div v-if="highPriorityIssues > 0" class="high-priority-alert">
              <el-icon><Warning /></el-icon>
              <span>当前有 <strong>{{ highPriorityIssues }}</strong> 个高优先级工单需要紧急处理</span>
            </div>
            <el-table :data="filteredIssueReports" stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="scooterId" label="车辆ID" width="100" />
              <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
              <el-table-column prop="priority" label="优先级" width="100">
                <template #default="{ row }">
                  <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="getIssueStatusType(row.status)" size="small">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="时间" width="160" />
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link @click="openProcess(row)">处理</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 用户反馈 -->
        <div v-if="activeTab === 'feedback'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>用户反馈</h3>
              <div class="header-actions">
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <el-table :data="feedbacks" stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="description" label="内容" min-width="240" show-overflow-tooltip />
              <el-table-column prop="priority" label="优先级" width="100">
                <template #default="{ row }">
                  <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="110" />
              <el-table-column prop="adminResponse" label="回复" min-width="180" show-overflow-tooltip />
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link @click="openProcess(row)">处理</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 用户分析 -->
        <div v-if="activeTab === 'userAnalysis'" class="tab-content">
          <div class="stats-grid">
            <div class="stat-card success">
              <div class="stat-icon"><el-icon><User /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ totalUsers }}</span>
                <span class="stat-label">总用户数</span>
              </div>
            </div>
            <div class="stat-card primary">
              <div class="stat-icon"><el-icon><UserFilled /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ todayNewUsers }}</span>
                <span class="stat-label">今日新增</span>
              </div>
            </div>
            <div class="stat-card warning">
              <div class="stat-icon"><el-icon><TrendCharts /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ weekNewUsers }}</span>
                <span class="stat-label">本周新增</span>
              </div>
            </div>
          </div>
          <div class="overview-row">
            <div class="chart-card">
              <div class="card-header"><h3>用户增长趋势</h3></div>
              <div ref="chartUserGrowthRef" class="chart"></div>
            </div>
            <div class="chart-card">
              <div class="card-header"><h3>用户活跃度 TOP 10</h3></div>
              <div class="user-ranking">
                <div v-for="(user, idx) in topActiveUsers" :key="user.id" class="ranking-item">
                  <span class="ranking-num" :class="{ gold: idx === 0, silver: idx === 1, bronze: idx === 2 }">{{ idx + 1 }}</span>
                  <span class="ranking-name">{{ user.username }}</span>
                  <span class="ranking-value">{{ user.bookingCount }} 次</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单分析 -->
        <div v-if="activeTab === 'orderAnalysis'" class="tab-content">
          <div class="stats-grid">
            <div class="stat-card primary">
              <div class="stat-icon"><el-icon><Tickets /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ totalBookings }}</span>
                <span class="stat-label">总订单数</span>
              </div>
            </div>
            <div class="stat-card success">
              <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ completedBookings }}</span>
                <span class="stat-label">已完成</span>
              </div>
            </div>
            <div class="stat-card warning">
              <div class="stat-icon"><el-icon><Clock /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ cancelledBookings }}</span>
                <span class="stat-label">已取消</span>
              </div>
            </div>
            <div class="stat-card info">
              <div class="stat-icon"><el-icon><Coin /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">¥{{ avgOrderValue }}</span>
                <span class="stat-label">平均订单金额</span>
              </div>
            </div>
          </div>
          <div class="overview-row">
            <div class="chart-card">
              <div class="card-header"><h3>订单状态分布</h3></div>
              <div ref="chartOrderStatusRef" class="chart"></div>
            </div>
            <div class="chart-card">
              <div class="card-header"><h3>热门预订时段</h3></div>
              <div ref="chartPeakHoursRef" class="chart"></div>
            </div>
          </div>
          <div class="overview-row">
            <div class="chart-card">
              <div class="card-header">
                <h3>一周内热门租赁日</h3>
                <el-tag v-if="hottestDay !== '无数据'" type="danger">{{ hottestDay }} 最热门 ({{ hottestDayCount }} 单)</el-tag>
              </div>
              <div ref="chartHotDaysRef" class="chart"></div>
            </div>
            <div class="chart-card">
              <div class="card-header"><h3>每日收入明细</h3></div>
              <div ref="chartDailyBreakdownRef" class="chart"></div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 处理反馈弹窗 -->
    <el-dialog v-model="processVisible" title="处理问题" width="520px" class="process-dialog">
      <el-form v-if="processRow" :model="processForm" label-width="100px">
        <el-form-item label="处理状态">
          <el-select v-model="processForm.status" style="width: 100%">
            <el-option v-if="processRow.scooterId != null" label="待处理 (PENDING)" value="PENDING" />
            <el-option v-else label="待处理 (OPEN)" value="OPEN" />
            <el-option label="处理中 (IN_PROGRESS)" value="IN_PROGRESS" />
            <el-option label="已解决 (RESOLVED)" value="RESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="processForm.priority" style="width: 100%">
            <el-option v-if="processRow.scooterId != null" label="普通 (NORMAL)" value="NORMAL" />
            <el-option label="低 (LOW)" value="LOW" />
            <el-option label="中 (MEDIUM)" value="MEDIUM" />
            <el-option label="高 (HIGH)" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理员回复">
          <el-input v-model="processForm.adminResponse" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" :loading="processLoading" @click="submitProcess">提交处理</el-button>
      </template>
    </el-dialog>

    <!-- ID16: 电量配置弹窗 -->
    <el-dialog v-model="batteryDialogVisible" title="配置车辆电量" width="400px" class="battery-dialog">
      <el-form v-if="batteryRow" :model="batteryForm" label-width="80px">
        <el-form-item label="车辆编号">
          <span class="battery-scooter-number">{{ batteryRow.scooterNumber }}</span>
        </el-form-item>
        <el-form-item label="当前电量">
          <div class="battery-current">
            <div class="battery-bar-large">
              <div class="battery-fill-large" :class="getBatteryClass(batteryRow.batteryLevel)" :style="{ width: (batteryRow.batteryLevel || 0) + '%' }"></div>
            </div>
            <span class="battery-percentage">{{ batteryRow.batteryLevel || 0 }}%</span>
          </div>
        </el-form-item>
        <el-form-item label="设置电量">
          <el-slider
            v-model="batteryForm.batteryLevel"
            :min="0"
            :max="100"
            :step="5"
            show-stops
            :format-tooltip="(val) => val + '%'"
          />
          <div class="battery-presets">
            <el-button size="small" @click="batteryForm.batteryLevel = 100">100%</el-button>
            <el-button size="small" @click="batteryForm.batteryLevel = 75">75%</el-button>
            <el-button size="small" @click="batteryForm.batteryLevel = 50">50%</el-button>
            <el-button size="small" @click="batteryForm.batteryLevel = 25">25%</el-button>
            <el-button size="small" type="warning" @click="batteryForm.batteryLevel = 10">低电量</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batteryDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batteryLoading" @click="saveBatteryLevel">保存</el-button>
      </template>
    </el-dialog>

    <!-- 服务点管理对话框 -->
    <el-dialog v-model="depotDialogVisible" :title="depotDialogTitle" width="500px" destroy-on-close>
      <el-form :model="depotForm" label-width="100px">
        <el-form-item label="服务点编号" required>
          <el-input v-model="depotForm.depotNumber" placeholder="如 D001" />
        </el-form-item>
        <el-form-item label="服务点名称" required>
          <el-input v-model="depotForm.name" placeholder="如 服务点 A（地铁站A口）" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="depotForm.address" placeholder="详细地址描述" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input-number v-model="depotForm.latitude" :precision="6" :step="0.001" :min="-90" :max="90" style="width: 100%" />
        </el-form-item>
        <el-form-item label="经度">
          <el-input-number v-model="depotForm.longitude" :precision="6" :step="0.001" :min="-180" :max="180" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大容量">
          <el-input-number v-model="depotForm.capacity" :min="1" :max="200" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="depotForm.status">
            <el-radio value="ACTIVE">正常</el-radio>
            <el-radio value="INACTIVE">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="depotDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="depotSubmitting" @click="submitDepot">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage, ElMessageBox } from 'element-plus'
import AMapLoader from '@amap/amap-jsapi-loader'
import {
  DataLine, TrendCharts, User, Van, LocationFilled, Tickets, Money, Sell,
  Warning, WarningFilled, ChatDotRound, SwitchButton, Refresh, HomeFilled, Top,
  Calendar, Clock, Search, MapLocation, Tools, UserFilled, CircleCheck, Coin, Menu,
  Plus, InfoFilled
} from '@element-plus/icons-vue'
import {
  adminCreateBooking,
  getWeeklyIncomeReport,
  getDailyIncomeReport,
  getAllFeedbacks,
  processFeedback,
  getAdminPricing,
  updateAdminPricing,
  getOvertimeFees,
  updateOvertimeFee,
  getStatisticsOverview,
  getStatisticsUsers,
  getStatisticsBookings,
  getStatisticsScooters,
  getStatisticsUserGrowth,
  getStatisticsTopUsers,
  getStatisticsBookingStatus,
  getStatisticsPeakHours,
  getWeeklyHotDays,
  getDailyIncomeBreakdown
} from '@/api/admin'
import { listUsers, updateUserStatus } from '@/api/user'
import { getScooters, updateScooterStatus, updateScooterBattery } from '@/api/scooter'
import { listHireOptions, updateHireOption } from '@/api/hireOptions'
import { getDepots, createDepot, updateDepot, deleteDepot } from '@/api/depot'
import { listDiscounts } from '@/api/discount'
import { updateIssueReport } from '@/api/issues'
import request from '@/utils/request'

const router = useRouter()

// 状态
const activeTab = ref('overview')
const loading = ref(false)
const overtimeLoading = ref(false)
const saving = ref(false)
const adminName = ref('管理员')
const userSearch = ref('')
const mobileMenuVisible = ref(false)

// 数据
const users = ref([])
const scooters = ref([])
const depots = ref([])
const pricingList = ref([])
const hireOptions = ref([])
const overtimeFees = ref([])
const feedbacks = ref([])
const issueReports = ref([])
const issuePriorityFilter = ref('')
const discounts = ref([])

// 超时费用弹窗
const overtimeDialogVisible = ref(false)
const overtimeForm = ref({
  id: null,
  hireOption: '',
  hireOptionName: '',
  feeType: 'HOURLY',
  fee: 0,
  maxOvertimeMinutes: null,
  enabled: true
})

// 服务点管理状态
const depotDialogVisible = ref(false)
const depotForm = ref({
  id: null,
  depotNumber: '',
  name: '',
  address: '',
  latitude: '',
  longitude: '',
  capacity: 10,
  status: 'ACTIVE'
})
const depotDialogTitle = ref('新增服务点')
const depotSubmitting = ref(false)

// 统计数据
const totalIncome = ref('0.00')
const weeklyIncome = ref('0.00')
const todayIncome = ref('0.00')
const totalUsers = ref(0)
const activeBookings = ref(0)
const todayNewUsers = ref(0)
const weekNewUsers = ref(0)
const usageRate = ref(0)
const avgRideDuration = ref(0)
const completionRate = ref(0)
const totalBookings = ref(0)
const completedBookings = ref(0)
const cancelledBookings = ref(0)
const avgOrderValue = ref('0.00')

// 图表
const chartByOptionRef = ref(null)
const chartDailyRef = ref(null)
const chartByOptionRef2 = ref(null)
const chartDailyRef2 = ref(null)
const chartUserGrowthRef = ref(null)
const chartOrderStatusRef = ref(null)
const chartPeakHoursRef = ref(null)
const chartHotDaysRef = ref(null)
const chartDailyBreakdownRef = ref(null)
let chartOption = null
let chartDaily = null
let chartUserGrowth = null
let chartOrderStatus = null
let chartPeakHours = null
let chartHotDays = null
let chartDailyBreakdown = null

// 地图
let adminMap = null
let AMapInstance = null
let scooterMarkers = []
let adminRefreshTimer = null
const DEFAULT_LNG = 116.397428
const DEFAULT_LAT = 39.90923

// 用户排名
const topActiveUsers = ref([])

// ID7: 代客预订（支持已注册和未注册用户）
const staffForm = ref({
  userId: null,
  scooterId: null,
  hireOption: '1hr',
  startTime: new Date(),
  bookingType: 'REGISTERED',  // REGISTERED / GUEST
  guestName: '',
  guestPhone: '',
  guestEmail: ''
})
const staffLoading = ref(false)

// ID7: 预订类型切换时清空相关字段
const onBookingTypeChange = () => {
  if (staffForm.value.bookingType === 'REGISTERED') {
    staffForm.value.guestName = ''
    staffForm.value.guestPhone = ''
    staffForm.value.guestEmail = ''
  } else {
    staffForm.value.userId = null
  }
}

// 处理反馈
const processVisible = ref(false)
const processRow = ref(null)
const processForm = ref({ status: 'OPEN', priority: 'LOW', adminResponse: '' })
const processLoading = ref(false)

// ID16: 电量配置
const batteryDialogVisible = ref(false)
const batteryRow = ref(null)
const batteryForm = ref({ batteryLevel: 100 })
const batteryLoading = ref(false)

// 热门租赁日统计
const hottestDay = ref('无数据')
const hottestDayCount = ref(0)

// 统计数据计算
const availableCount = computed(() => scooters.value.filter(s => s.status === 'AVAILABLE').length)
const inUseCount = computed(() => scooters.value.filter(s => s.status === 'IN_USE').length)
const maintenanceCount = computed(() => scooters.value.filter(s => s.status === 'MAINTENANCE').length)

// IssueReports 使用 PENDING, Feedback 使用 OPEN
const pendingIssues = computed(() => issueReports.value.filter(i => i.status === 'PENDING').length)
const openIssues = computed(() => issueReports.value.filter(i => i.status === 'PENDING').length)
const inProgressIssues = computed(() => issueReports.value.filter(i => i.status === 'IN_PROGRESS').length)
const resolvedIssues = computed(() => issueReports.value.filter(i => i.status === 'RESOLVED').length)

// ID14: 高优先级工单数量
const highPriorityIssues = computed(() => issueReports.value.filter(i => i.priority === 'HIGH').length)

// ID14: 根据优先级筛选工单
const filteredIssueReports = computed(() => {
  if (!issuePriorityFilter.value) return issueReports.value
  return issueReports.value.filter(i => i.priority === issuePriorityFilter.value)
})

const filteredUsers = computed(() => {
  if (!userSearch.value) return users.value
  const kw = userSearch.value.toLowerCase()
  return users.value.filter(u =>
    (u.username && u.username.toLowerCase().includes(kw)) ||
    (u.email && u.email.toLowerCase().includes(kw))
  )
})

const availableScooters = computed(() => scooters.value.filter(s => s.status === 'AVAILABLE'))

// ID7: 代客预订车辆选择器分组显示
const scooterOptions = computed(() => {
  const available = scooters.value.filter(s => s.status === 'AVAILABLE')
  const inUse = scooters.value.filter(s => s.status === 'IN_USE')
  const maintenance = scooters.value.filter(s => s.status === 'MAINTENANCE')
  const groups = []
  if (available.length > 0) {
    groups.push({ label: `✓ 可用车辆 (${available.length})`, options: available })
  }
  if (inUse.length > 0) {
    groups.push({ label: `✗ 使用中 (${inUse.length})`, options: inUse })
  }
  if (maintenance.length > 0) {
    groups.push({ label: `🔧 维护中 (${maintenance.length})`, options: maintenance })
  }
  return groups
})

// ID7: 获取已选车辆信息
const getSelectedScooterInfo = () => {
  if (!staffForm.value.scooterId) return ''
  const scooter = scooters.value.find(s => s.id === staffForm.value.scooterId)
  if (!scooter) return ''
  return `${scooter.scooterNumber} - ${getStatusText(scooter.status)} - ${scooter.batteryLevel !== null ? '电量' + scooter.batteryLevel + '%' : '电量未知'}`
}

// 预警计算
const lowBatteryScooters = computed(() => scooters.value.filter(s => s.batteryLevel && s.batteryLevel < 20))
const maintenanceNeeded = computed(() => scooters.value.filter(s => s.status === 'MAINTENANCE'))
const lowStockDepots = computed(() => depots.value.filter(d => {
  if (!d.capacity || d.capacity <= 0) return false
  const ratio = d.currentStock / d.capacity
  return ratio < 0.3 // 库存不足（低于30%）
}))
const overflowDepots = computed(() => depots.value.filter(d => {
  if (!d.capacity || d.capacity <= 0) return false
  return d.currentStock > d.capacity // 库存溢出（超过容量）
}))

// 工具函数
const getStatusType = (status) => {
  const map = { AVAILABLE: 'success', RESERVED: 'warning', IN_USE: 'warning', MAINTENANCE: 'info', RETIRED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { AVAILABLE: '可用', RESERVED: '已预订', IN_USE: '使用中', MAINTENANCE: '维护中', RETIRED: '退役' }
  return map[status] || status
}

const getBatteryClass = (level) => {
  if (!level) return 'low'
  if (level >= 60) return 'high'
  if (level >= 30) return 'medium'
  return 'low'
}

const getStockClass = (current, capacity) => {
  const ratio = current / capacity
  if (ratio > 0.5) return 'good'
  if (ratio > 0.2) return 'warning'
  return 'danger'
}

const getDepotName = (id) => {
  const depot = depots.value.find(d => d.id === id)
  return depot ? depot.name : '—'
}

const getPriorityType = (priority) => {
  // feedback 使用 LOW, MEDIUM, HIGH
  // issue_report 使用 LOW, NORMAL, HIGH
  const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info', NORMAL: 'info' }
  return map[priority] || 'info'
}

const getIssueStatusType = (status) => {
  // issue_report 使用 PENDING, IN_PROGRESS, RESOLVED
  // feedback 使用 OPEN, IN_PROGRESS, RESOLVED
  const map = { PENDING: 'warning', OPEN: 'warning', IN_PROGRESS: 'primary', RESOLVED: 'success' }
  return map[status] || 'info'
}

// Tab切换
const switchTab = async (tab) => {
  activeTab.value = tab
  await nextTick()
  if (tab === 'overview') {
    // 运营概览页面刷新图表
    if (chartOption) chartOption.resize()
    if (chartDaily) chartDaily.resize()
    renderReportCharts()
  } else if (tab === 'reports') renderReportCharts()
  else if (tab === 'userAnalysis') renderUserAnalysisCharts()
  else if (tab === 'orderAnalysis') renderOrderAnalysisCharts()
  else if (tab === 'map') initAdminMap()
}

// 刷新数据
const refreshData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadUsers(),
      loadScooters(),
      loadDepots(),
      loadPricing(),
      loadHireOptions(),
      loadOvertimeFees(),
      loadFeedbacks(),
      loadIssues(),
      // loadDiscounts(), // TODO: 后端接口修复后再启用
      loadReports()
    ])
    // 刷新地图标记
    if (activeTab.value === 'map') {
      updateAdminScooterMarkers()
    }
  } finally {
    loading.value = false
  }
}

// 启动自动刷新
const startAdminAutoRefresh = () => {
  adminRefreshTimer = setInterval(() => {
    // 只刷新车辆数据（实时性要求高）
    loadScooters()
  }, 15000) // 15秒刷新一次
}

// 停止自动刷新
const stopAdminAutoRefresh = () => {
  if (adminRefreshTimer) {
    clearInterval(adminRefreshTimer)
    adminRefreshTimer = null
  }
}

// 加载数据
const loadUsers = async () => {
  try {
    const res = await listUsers()
    users.value = Array.isArray(res) ? res.filter(u => u.role !== 'ADMIN') : []
    totalUsers.value = users.value.length
  } catch (e) { console.error(e) }
}

const loadScooters = async () => {
  try {
    const res = await getScooters()
    const newList = Array.isArray(res) ? res : []
    // 检查状态变化
    newList.forEach(newScooter => {
      const oldScooter = scooters.value.find(s => s.id === newScooter.id)
      if (oldScooter && oldScooter.status !== newScooter.status) {
        if (newScooter.status === 'IN_USE') {
          ElMessage.info(`车辆 ${newScooter.scooterNumber} 已被租用`)
        } else if (newScooter.status === 'AVAILABLE') {
          ElMessage.info(`车辆 ${newScooter.scooterNumber} 已归还可用`)
        }
      }
    })
    scooters.value = newList
  } catch (e) { console.error(e) }
}

const loadDepots = async () => {
  try {
    const res = await getDepots()
    depots.value = Array.isArray(res) ? res : []
  } catch (e) { console.error(e) }
}

// 服务点管理函数
const openAddDepotDialog = () => {
  depotForm.value = {
    id: null,
    depotNumber: '',
    name: '',
    address: '',
    latitude: '',
    longitude: '',
    capacity: 10,
    status: 'ACTIVE'
  }
  depotDialogTitle.value = '新增服务点'
  depotDialogVisible.value = true
}

const openEditDepotDialog = (row) => {
  depotForm.value = {
    id: row.id,
    depotNumber: row.depotNumber || '',
    name: row.name || '',
    address: row.address || '',
    latitude: row.latitude || '',
    longitude: row.longitude || '',
    capacity: row.capacity || 10,
    status: row.status || 'ACTIVE'
  }
  depotDialogTitle.value = '编辑服务点'
  depotDialogVisible.value = true
}

const submitDepot = async () => {
  if (!depotForm.value.depotNumber || !depotForm.value.name) {
    ElMessage.warning('请填写服务点编号和名称')
    return
  }
  depotSubmitting.value = true
  try {
    const data = {
      depotNumber: depotForm.value.depotNumber,
      name: depotForm.value.name,
      address: depotForm.value.address,
      latitude: parseFloat(depotForm.value.latitude) || 0,
      longitude: parseFloat(depotForm.value.longitude) || 0,
      capacity: parseInt(depotForm.value.capacity) || 10,
      status: depotForm.value.status
    }
    if (depotForm.value.id) {
      await updateDepot(depotForm.value.id, data)
      ElMessage.success('更新成功')
    } else {
      await createDepot(data)
      ElMessage.success('创建成功')
    }
    depotDialogVisible.value = false
    await loadDepots()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  } finally {
    depotSubmitting.value = false
  }
}

const handleDeleteDepot = async (row) => {
  if (row.totalScooters > 0) {
    ElMessage.warning('该服务点下仍有车辆，无法删除')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除服务点「${row.name}」吗？`,
      '删除确认',
      { type: 'warning' }
    )
    await deleteDepot(row.id)
    ElMessage.success('删除成功')
    await loadDepots()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.response?.data?.message || '删除失败')
    }
  }
}

const loadPricing = async () => {
  try {
    const res = await getAdminPricing()
    pricingList.value = Array.isArray(res) ? res.map(r => ({ ...r, price: Number(r.price) })) : []
  } catch (e) { console.error(e) }
}

const loadHireOptions = async () => {
  try {
    const res = await listHireOptions()
    hireOptions.value = Array.isArray(res) ? res.map(r => ({ ...r, price: Number(r.price) })) : []
  } catch (e) { console.error(e) }
}

const loadOvertimeFees = async () => {
  overtimeLoading.value = true
  try {
    const res = await getOvertimeFees()
    overtimeFees.value = Array.isArray(res) ? res : []
  } catch (e) { console.error(e) }
  finally { overtimeLoading.value = false }
}

const loadFeedbacks = async () => {
  try {
    const res = await getAllFeedbacks()
    console.log('Feedbacks response:', res)
    feedbacks.value = Array.isArray(res) ? res : []
  } catch (e) {
    console.error('Failed to load feedbacks:', e)
    feedbacks.value = []
  }
}

const loadIssues = async () => {
  try {
    const res = await request({ url: '/api/issues', method: 'get' })
    issueReports.value = Array.isArray(res) ? res : []
  } catch (e) {
    console.error('Failed to load issues:', e)
    issueReports.value = []
  }
}

const loadDiscounts = async () => {
  try {
    const res = await listDiscounts()
    discounts.value = Array.isArray(res) ? res : []
  } catch (e) { console.error(e) }
}

const loadReports = async () => {
  try {
    // 使用新的统计 API
    const overview = await getStatisticsOverview()
    const userStats = await getStatisticsUsers()

    // 填充概览数据
    totalIncome.value = (overview?.totalIncome || 0).toFixed(2)
    weeklyIncome.value = (overview?.weekIncome || 0).toFixed(2)
    todayIncome.value = (overview?.todayIncome || 0).toFixed(2)
    totalUsers.value = overview?.totalUsers || 0
    activeBookings.value = overview?.activeBookings || 0
    usageRate.value = overview?.usageRate || 0
    avgRideDuration.value = overview?.avgRideDuration || 0
    completionRate.value = overview?.completionRate || 0

    // 用户统计
    todayNewUsers.value = userStats?.todayNewUsers || 0
    weekNewUsers.value = userStats?.weekNewUsers || 0

    // 订单统计
    const bookingStats = await getStatisticsBookings()
    totalBookings.value = bookingStats?.totalBookings || 0
    completedBookings.value = bookingStats?.completedBookings || 0
    cancelledBookings.value = bookingStats?.cancelledBookings || 0
    avgOrderValue.value = (bookingStats?.avgOrderValue || 0).toFixed(2)

    // 活跃用户排行
    const topUsersRes = await getStatisticsTopUsers()
    topActiveUsers.value = topUsersRes?.topUsers || []

    // 获取收入报表数据
    const weekly = await getWeeklyIncomeReport()
    const daily = await getDailyIncomeReport()

    await nextTick()
    renderReportCharts()
  } catch (e) {
    console.error('加载统计数据失败:', e)
    // 备用：使用旧接口
    try {
      const weekly = await getWeeklyIncomeReport()
      const daily = await getDailyIncomeReport()
      const byOpt = weekly?.incomeByHireOption || {}
      const optValues = Object.values(byOpt)
      const total = optValues.reduce((sum, v) => sum + Number(v || 0), 0)
      totalIncome.value = total.toFixed(2)
      weeklyIncome.value = total.toFixed(2)
      const today = new Date().toISOString().split('T')[0]
      const dailyMap = daily?.dailyIncome || {}
      todayIncome.value = (Number(dailyMap[today]) || 0).toFixed(2)
      activeBookings.value = inUseCount.value
      renderReportCharts()
    } catch (e2) {
      console.error('备用加载也失败:', e2)
    }
  }
}

// 渲染收入报表图表
const renderReportCharts = () => {
  nextTick(() => {
    Promise.all([getWeeklyIncomeReport(), getDailyIncomeReport()]).then(([weekly, daily]) => {
      const byOpt = weekly?.incomeByHireOption || {}
      const dailyMap = daily?.dailyIncome || {}

      // 各租期收入分布
      const optKeys = Object.keys(byOpt)
      const optVals = Object.values(byOpt)
      const hasOptData = optKeys.length > 0 && optVals.some(v => Number(v) > 0)

      ;[chartByOptionRef.value, chartByOptionRef2.value].forEach(ref => {
        if (ref) {
          const chart = echarts.getInstanceByDom(ref) || echarts.init(ref)
          if (hasOptData) {
            chart.setOption({
              color: ['#1e3a5f', '#3b5998', '#6b9ac4', '#94a3b8'],
              tooltip: { trigger: 'axis' },
              xAxis: { type: 'category', data: optKeys, axisLabel: { rotate: 30 } },
              yAxis: { type: 'value', name: '金额 (¥)' },
              series: [{ type: 'bar', data: optVals, itemStyle: { borderRadius: [4, 4, 0, 0] } }]
            })
          } else {
            chart.setOption({
              title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14 } },
              xAxis: { type: 'category', data: [] },
              yAxis: { type: 'value', name: '金额 (¥)' },
              series: [{ type: 'bar', data: [] }]
            })
          }
        }
      })

      // 每日收入趋势
      const days = Object.keys(dailyMap).sort()
      const dailyVals = days.map(d => dailyMap[d])
      const hasDailyData = days.length > 0 && dailyVals.some(v => Number(v) > 0)

      ;[chartDailyRef.value, chartDailyRef2.value].forEach(ref => {
        if (ref) {
          const chart = echarts.getInstanceByDom(ref) || echarts.init(ref)
          if (hasDailyData) {
            chart.setOption({
              color: ['#6b9ac4'],
              tooltip: { trigger: 'axis' },
              xAxis: { type: 'category', data: days, axisLabel: { rotate: 30 } },
              yAxis: { type: 'value', name: '金额 (¥)' },
              series: [{
                type: 'line',
                smooth: true,
                areaStyle: { opacity: 0.3 },
                data: dailyVals
              }]
            })
          } else {
            chart.setOption({
              title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#94a3b8', fontSize: 14 } },
              xAxis: { type: 'category', data: [] },
              yAxis: { type: 'value', name: '金额 (¥)' },
              series: [{ type: 'line', smooth: true, areaStyle: { opacity: 0.3 }, data: [] }]
            })
          }
        }
      })
    }).catch(err => {
      console.error('加载报表数据失败:', err)
    })
  })
}

// 用户分析图表
const renderUserAnalysisCharts = () => {
  nextTick(() => {
    if (chartUserGrowthRef.value) {
      const chart = echarts.getInstanceByDom(chartUserGrowthRef.value) || echarts.init(chartUserGrowthRef.value)

      // 获取用户增长数据
      getStatisticsUserGrowth().then(res => {
        const dailyData = res?.dailyNewUsers || {}
        const days = Object.keys(dailyData)
        const data = Object.values(dailyData)

        chart.setOption({
          color: ['#1e3a5f'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: days },
          yAxis: { type: 'value', name: '用户数' },
          series: [{
            type: 'bar',
            data,
            itemStyle: { borderRadius: [4, 4, 0, 0] }
          }]
        })
      }).catch(() => {
        // 备用模拟数据
        const days = Array.from({ length: 7 }, (_, i) => {
          const d = new Date()
          d.setDate(d.getDate() - (6 - i))
          return `${d.getMonth() + 1}/${d.getDate()}`
        })
        const data = Array.from({ length: 7 }, () => Math.floor(Math.random() * 10) + 1)
        chart.setOption({
          color: ['#1e3a5f'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: days },
          yAxis: { type: 'value', name: '用户数' },
          series: [{ type: 'bar', data, itemStyle: { borderRadius: [4, 4, 0, 0] } }]
        })
      })
    }
  })
}

// 订单分析图表
const renderOrderAnalysisCharts = () => {
  nextTick(() => {
    // 订单状态饼图
    if (chartOrderStatusRef.value) {
      const chart = echarts.getInstanceByDom(chartOrderStatusRef.value) || echarts.init(chartOrderStatusRef.value)
      chart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: { show: true, formatter: '{b}: {c} ({d}%)' },
          data: [
            { value: completedBookings.value, name: '已完成', itemStyle: { color: '#2d8a4e' } },
            { value: activeBookings.value, name: '进行中', itemStyle: { color: '#c4880c' } },
            { value: cancelledBookings.value, name: '已取消', itemStyle: { color: '#d14545' } }
          ]
        }]
      })
    }

    // 热门时段图
    if (chartPeakHoursRef.value) {
      const chart = echarts.getInstanceByDom(chartPeakHoursRef.value) || echarts.init(chartPeakHoursRef.value)

      getStatisticsPeakHours().then(res => {
        const peakData = res?.peakHours || {}
        const hours = Object.keys(peakData)
        const data = Object.values(peakData)
        chart.setOption({
          color: ['#6b9ac4'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: hours },
          yAxis: { type: 'value', name: '订单数' },
          series: [{ type: 'bar', data, itemStyle: { borderRadius: [4, 4, 0, 0] } }]
        })
      }).catch(() => {
        // 备用模拟数据
        const hours = ['8时', '10时', '12时', '14时', '16时', '18时', '20时']
        const data = [12, 25, 18, 15, 22, 30, 20]
        chart.setOption({
          color: ['#6b9ac4'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: hours },
          yAxis: { type: 'value', name: '订单数' },
          series: [{ type: 'bar', data, itemStyle: { borderRadius: [4, 4, 0, 0] } }]
        })
      })
    }

    // 热门租赁日图表
    if (chartHotDaysRef.value) {
      const chart = echarts.getInstanceByDom(chartHotDaysRef.value) || echarts.init(chartHotDaysRef.value)

      getWeeklyHotDays().then(res => {
        const hotDaysData = res?.hotDays || {}
        const days = Object.keys(hotDaysData)
        const data = Object.values(hotDaysData)

        // 更新最热门日期
        hottestDay.value = res?.hottestDay || '无数据'
        hottestDayCount.value = res?.hottestDayCount || 0

        chart.setOption({
          color: ['#1e3a5f', '#3b5998', '#6b9ac4', '#94a3b8'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: days },
          yAxis: { type: 'value', name: '订单数' },
          series: [{
            type: 'bar',
            data,
            itemStyle: {
              borderRadius: [4, 4, 0, 0],
              color: (params) => {
                if (days[params.dataIndex] === hottestDay.value && hottestDay.value !== '无数据') {
                  return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#d14545' },
                    { offset: 1, color: '#ef4444' }
                  ])
                }
                return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#1e3a5f' },
                  { offset: 1, color: '#3b5998' }
                ])
              }
            }
          }]
        })
      }).catch(() => {
        const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
        const data = [5, 12, 8, 15, 10, 18, 22]
        chart.setOption({
          color: ['#1e3a5f'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: days },
          yAxis: { type: 'value', name: '订单数' },
          series: [{ type: 'bar', data, itemStyle: { borderRadius: [4, 4, 0, 0] } }]
        })
      })
    }

    // 每日收入明细堆叠图
    if (chartDailyBreakdownRef.value) {
      const chart = echarts.getInstanceByDom(chartDailyBreakdownRef.value) || echarts.init(chartDailyBreakdownRef.value)

      getDailyIncomeBreakdown().then(res => {
        const breakdown = res?.dailyBreakdown || {}
        const dates = Object.keys(breakdown)
        const option1hr = dates.map(d => breakdown[d]?.['1hr'] || 0)
        const option4hr = dates.map(d => breakdown[d]?.['4hr'] || 0)
        const option1day = dates.map(d => breakdown[d]?.['1day'] || 0)
        const option1week = dates.map(d => breakdown[d]?.['1week'] || 0)

        chart.setOption({
          color: ['#1e3a5f', '#3b5998', '#6b9ac4', '#94a3b8'],
          tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'shadow' },
            formatter: (params) => {
              let total = 0
              let html = params[0].name + '<br/>'
              params.forEach(p => {
                total += p.value
                html += p.marker + p.seriesName + ': ¥' + p.value.toFixed(2) + '<br/>'
              })
              html += '<strong>总计: ¥' + total.toFixed(2) + '</strong>'
              return html
            }
          },
          legend: { data: ['1小时', '4小时', '1天', '1周'] },
          xAxis: { type: 'category', data: dates },
          yAxis: { type: 'value', name: '收入 (¥)' },
          series: [
            { name: '1小时', type: 'bar', stack: 'total', data: option1hr },
            { name: '4小时', type: 'bar', stack: 'total', data: option4hr },
            { name: '1天', type: 'bar', stack: 'total', data: option1day },
            { name: '1周', type: 'bar', stack: 'total', data: option1week }
          ]
        })
      }).catch(() => {
        const dates = ['05-13', '05-14', '05-15', '05-16', '05-17', '05-18', '05-19']
        chart.setOption({
          color: ['#1e3a5f', '#3b5998', '#6b9ac4', '#94a3b8'],
          tooltip: { trigger: 'axis' },
          legend: { data: ['1小时', '4小时', '1天', '1周'] },
          xAxis: { type: 'category', data: dates },
          yAxis: { type: 'value', name: '收入 (¥)' },
          series: [
            { name: '1小时', type: 'bar', stack: 'total', data: [120, 150, 100, 80, 200, 180, 220] },
            { name: '4小时', type: 'bar', stack: 'total', data: [80, 120, 150, 100, 150, 200, 180] },
            { name: '1天', type: 'bar', stack: 'total', data: [60, 80, 100, 120, 100, 150, 200] },
            { name: '1周', type: 'bar', stack: 'total', data: [40, 60, 80, 100, 80, 100, 120] }
          ]
        })
      })
    }
  })
}

// 初始化管理地图（使用高德地图）
const initAdminMap = async () => {
  nextTick(async () => {
    const mapEl = document.getElementById('adminMap')
    if (!mapEl) return

    try {
      console.log('开始加载高德地图 API...')
      const AMap = await AMapLoader.load({
        key: import.meta.env.VITE_AMAP_KEY || '27ec2a64ff4acc99ccf61c8c897a69d3',
        version: '2.0'
      })
      console.log('高德地图 API 加载完成')

      // 如果已有地图实例，先销毁
      if (adminMap) {
        adminMap.destroy()
      }

      adminMap = new AMap.Map('adminMap', {
        zoom: 13,
        center: [DEFAULT_LNG, DEFAULT_LAT],
        resizeEnable: true
      })

      AMapInstance = AMap
      scooterMarkers = []

      // 添加车辆标记
      updateAdminScooterMarkers()

    } catch (err) {
      console.error('管理地图加载失败', err)
      // 降级：显示占位图
      mapEl.innerHTML = `
        <div class="simple-map">
          <div class="map-placeholder">
            <el-icon size="48"><MapLocation /></el-icon>
            <p>车辆分布地图</p>
            <p class="map-hint">共 ${scooters.value.length} 辆车</p>
          </div>
        </div>
      `
    }
  })
}

// 更新管理端车辆标记
const updateAdminScooterMarkers = () => {
  if (!adminMap || !AMapInstance) return

  // 清除旧标记
  scooterMarkers.forEach(m => adminMap.remove(m))
  scooterMarkers = []

  const statusColors = {
    'AVAILABLE': '#2d8a4e',
    'IN_USE': '#c4880c',
    'MAINTENANCE': '#94a3b8',
    'RETIRED': '#dc2626'
  }

  const statusLabels = {
    'AVAILABLE': '可用',
    'IN_USE': '使用中',
    'MAINTENANCE': '维护中',
    'RETIRED': '退役'
  }

  // 遍历所有车辆，添加标记
  scooters.value.forEach(scooter => {
    // 如果有经纬度，使用真实位置
    let lng = scooter.longitude || scooter.lng || DEFAULT_LNG
    let lat = scooter.latitude || scooter.lat || DEFAULT_LAT

    // 确保经纬度在合理范围内（中国）
    if (lng < 73 || lng > 135 || lat < 15 || lat > 54) {
      // 如果坐标不合理，使用服务点位置或默认位置
      const depot = depots.value.find(d => d.id === scooter.depotId)
      if (depot) {
        lng = depot.longitude || depot.lng || DEFAULT_LNG
        lat = depot.latitude || depot.lat || DEFAULT_LAT
      }
    }

    const color = statusColors[scooter.status] || '#94a3b8'
    const label = statusLabels[scooter.status] || '未知'
    const statusText = label

    const marker = new AMap.Marker({
      position: [lng, lat],
      title: `${scooter.scooterNumber} - ${statusText}`,
      offset: new AMap.Pixel(-16, -32),
      content: `
        <div style="
          background: ${color};
          color: white;
          padding: 4px 8px;
          border-radius: 4px;
          font-size: 12px;
          white-space: nowrap;
          box-shadow: 0 2px 6px rgba(0,0,0,0.3);
        ">
          ${scooter.scooterNumber}
        </div>
      `
    })

    marker.on('click', () => {
      ElMessage({
        message: `车牌: ${scooter.scooterNumber} | 状态: ${statusText} | 位置: ${scooter.location || '未知'}`,
        type: 'info'
      })
    })

    adminMap.add(marker)
    scooterMarkers.push(marker)
  })

  // 如果有车辆，自动调整视野
  if (scooters.value.length > 0) {
    adminMap.setFitView()
  }
}

// 用户状态切换
const toggleUserStatus = async (row) => {
  try {
    await updateUserStatus(row.id, !row.isActive)
    row.isActive = !row.isActive
    ElMessage.success(`用户已${row.isActive ? '启用' : '禁用'}`)
  } catch (e) { console.error(e) }
}

// 更新车辆状态
const setStatus = async (id, status) => {
  try {
    await updateScooterStatus(id, status)
    ElMessage.success('状态已更新')
    await loadScooters()
  } catch (e) { console.error(e) }
}

// ID16: 打开电量配置弹窗
const openBatteryDialog = (row) => {
  batteryRow.value = row
  batteryForm.value.batteryLevel = row.batteryLevel || 100
  batteryDialogVisible.value = true
}

// ID16: 保存电量配置
const saveBatteryLevel = async () => {
  if (!batteryRow.value) return
  batteryLoading.value = true
  try {
    await updateScooterBattery(batteryRow.value.id, batteryForm.value.batteryLevel)
    ElMessage.success('电量配置已保存')
    batteryDialogVisible.value = false
    await loadScooters()
  } catch (e) {
    console.error(e)
    ElMessage.error('电量配置保存失败')
  } finally {
    batteryLoading.value = false
  }
}

// ID7: 代客预订（支持已注册和未注册用户）
const submitStaffBooking = async () => {
  // ID7: 根据预订类型验证
  if (staffForm.value.bookingType === 'REGISTERED') {
    if (!staffForm.value.userId) {
      ElMessage.warning('请选择用户')
      return
    }
  } else {
    // Guest 预订验证
    if (!staffForm.value.guestName?.trim()) {
      ElMessage.warning('请输入访客姓名')
      return
    }
    if (!staffForm.value.guestPhone?.trim()) {
      ElMessage.warning('请输入联系电话')
      return
    }
  }
  if (!staffForm.value.scooterId || !staffForm.value.startTime) {
    ElMessage.warning('请填写完整信息')
    return
  }
  staffLoading.value = true
  try {
    await adminCreateBooking({
      userId: staffForm.value.userId,
      scooterId: staffForm.value.scooterId,
      hireOption: staffForm.value.hireOption,
      startTime: staffForm.value.startTime.toISOString(),
      // ID7: 未注册用户字段
      bookingType: staffForm.value.bookingType,
      guestName: staffForm.value.guestName,
      guestPhone: staffForm.value.guestPhone,
      guestEmail: staffForm.value.guestEmail
    })
    ElMessage.success('代客订单创建成功')
    // 刷新车辆列表，显示最新的状态变化
    loadScooters()
    staffForm.value = {
      userId: null,
      scooterId: null,
      hireOption: '1hr',
      startTime: new Date(),
      bookingType: 'REGISTERED',
      guestName: '',
      guestPhone: '',
      guestEmail: ''
    }
  } catch (e) {
    console.error('Failed to create booking:', e)
    ElMessage.error(e?.response?.data?.message || e?.message || '创建订单失败，请稍后重试')
  } finally {
    staffLoading.value = false
  }
}

// 保存价格
const savePricing = async (row) => {
  try {
    await updateAdminPricing({ id: row.id, hireOption: row.hireOption, price: row.price, description: row.description })
    ElMessage.success('价格已保存')
  } catch (e) { console.error(e) }
}

const saveHireOption = async (row) => {
  try {
    await updateHireOption(row.id, { code: row.code, label: row.label, durationMinutes: row.durationMinutes, price: row.price })
    ElMessage.success('租用选项已保存')
  } catch (e) { console.error(e) }
}

// 超时费用相关
const editOvertimeFee = (row) => {
  overtimeForm.value = { ...row }
  overtimeDialogVisible.value = true
}

const saveOvertimeFee = async () => {
  saving.value = true
  try {
    await updateOvertimeFee(overtimeForm.value)
    ElMessage.success('保存成功')
    overtimeDialogVisible.value = false
    await loadOvertimeFees()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const toggleOvertimeEnabled = async (row) => {
  try {
    await updateOvertimeFee({ ...row })
    ElMessage.success(row.enabled ? '已启用' : '已禁用')
  } catch (e) {
    row.enabled = !row.enabled
    ElMessage.error('操作失败')
  }
}

// 处理反馈
const openProcess = (row) => {
  processRow.value = row
  // 根据记录类型设置默认值
  const isIssue = row.scooterId != null && row.scooterId !== ''
  const defaultStatus = isIssue ? (row.status || 'PENDING') : (row.status || 'OPEN')
  const defaultPriority = isIssue ? (row.priority || 'NORMAL') : (row.priority || 'LOW')
  processForm.value = {
    status: defaultStatus,
    priority: defaultPriority,
    adminResponse: row.adminResponse || row.adminFeedback || ''
  }
  processVisible.value = true
}

const submitProcess = async () => {
  if (!processRow.value) return
  processLoading.value = true
  try {
    // 判断是故障工单还是用户反馈
    // issue_report 有 scooterId 字段，feedback 没有
    const isIssue = processRow.value.scooterId != null && processRow.value.scooterId !== ''
    if (isIssue) {
      // 故障工单使用 adminFeedback 字段
      await updateIssueReport(processRow.value.id, {
        status: processForm.value.status,
        priority: processForm.value.priority,
        adminFeedback: processForm.value.adminResponse
      })
    } else {
      // 用户反馈使用 adminResponse 字段
      await processFeedback(processRow.value.id, { ...processForm.value })
    }
    ElMessage.success('处理结果已保存')
    processVisible.value = false
    await Promise.all([loadFeedbacks(), loadIssues()])
  } catch (e) {
    console.error('Failed to process:', e)
    ElMessage.error('处理失败，请稍后重试')
  } finally {
    processLoading.value = false
  }
}

// 退出
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/admin/login')
}

// 初始化
onMounted(async () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      adminName.value = user.username || '管理员'
    } catch (e) {}
  }

  await refreshData()
  startAdminAutoRefresh()

  window.addEventListener('resize', () => {
    chartOption?.resize()
    chartDaily?.resize()
  })
})

onUnmounted(() => {
  chartOption?.dispose()
  chartDaily?.dispose()
  chartOrderStatus?.dispose()
  chartPeakHours?.dispose()
  chartHotDays?.dispose()
  chartDailyBreakdown?.dispose()
  if (adminMap) {
    adminMap.destroy()
    adminMap = null
  }
  stopAdminAutoRefresh()
})

// 监听车辆数据变化，自动更新地图标记
watch(scooters, () => {
  if (activeTab.value === 'map' && adminMap) {
    nextTick(() => updateAdminScooterMarkers())
  }
}, { deep: true })
</script>

<style scoped>
/* 布局 */
.admin-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(180deg, #e8eef5 0%, #d6e0eb 100%);
}

/* 侧边栏 */
.sidebar {
  width: 260px;
  background: white;
  border-right: 1px solid #d6e0eb;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  z-index: 100;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e8eef5;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name {
  font-size: 17px;
  font-weight: 800;
  color: #1e3a5f;
}

.brand-tag {
  font-size: 11px;
  color: #5a7a9a;
}

/* 导航 */
.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
}

.nav-section {
  margin-bottom: 24px;
}

.nav-label {
  display: block;
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 0 12px;
  margin-bottom: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  color: #5a7a9a;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
}

.nav-item:hover {
  background: #f0f4f8;
  color: #1e3a5f;
}

.nav-item.active {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  color: white;
}

.nav-item .el-icon {
  font-size: 18px;
}

.badge {
  margin-left: auto;
  background: #d14545;
  color: white;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 10px;
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #e8eef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.admin-avatar {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  color: white;
  font-weight: 700;
}

.admin-detail {
  display: flex;
  flex-direction: column;
}

.admin-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e3a5f;
}

.admin-role {
  font-size: 11px;
  color: #94a3b8;
}

.logout-btn {
  padding: 8px;
}

/* 主内容区 */
.main-area {
  flex: 1;
  margin-left: 260px;
  min-height: 100vh;
}

/* 内容区域 */
.content-wrapper {
  padding: 24px 28px;
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stats-grid.mini {
  grid-template-columns: repeat(4, 1fr);
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.stat-card.primary::before { background: linear-gradient(180deg, #1e3a5f, #3b5998); }
.stat-card.success::before { background: linear-gradient(180deg, #2d8a4e, #5cb885); }
.stat-card.warning::before { background: linear-gradient(180deg, #c4880c, #e6a82d); }
.stat-card.info::before { background: linear-gradient(180deg, #6b9ac4, #94b8d4); }

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-card.primary .stat-icon { background: rgba(30, 58, 95, 0.1); color: #1e3a5f; }
.stat-card.success .stat-icon { background: rgba(45, 138, 78, 0.1); color: #2d8a4e; }
.stat-card.warning .stat-icon { background: rgba(196, 136, 12, 0.1); color: #c4880c; }
.stat-card.info .stat-icon { background: rgba(107, 154, 196, 0.1); color: #6b9ac4; }

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 26px;
  font-weight: 800;
  color: #1e3a5f;
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
}

.stat-trend {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
}

.stat-trend.up { color: #2d8a4e; }
.stat-trend.down { color: #d14545; }

/* 小型统计 */
.stat-mini {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 2px 12px rgba(30, 58, 95, 0.06);
}

.stat-mini::before {
  content: '';
  width: 100%;
  height: 4px;
  border-radius: 2px;
  margin-bottom: 12px;
}

.stat-mini.available::before { background: #2d8a4e; }
.stat-mini.inuse::before { background: #5a7a9a; }
.stat-mini.maintenance::before { background: #c4880c; }
.stat-mini.total::before { background: #1e3a5f; }
.stat-mini.open::before { background: #c4880c; }
.stat-mini.progress::before { background: #3b5998; }
.stat-mini.resolved::before { background: #2d8a4e; }
/* ID14: 高优先级样式 */
.stat-mini.high-priority::before { background: #d14545; }
.stat-mini.high-priority .mini-value { color: #d14545; }

.mini-value {
  font-size: 28px;
  font-weight: 800;
  color: #1e3a5f;
}

.mini-label {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

/* 图表卡片 */
.overview-row, .chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e3a5f;
}

.chart {
  height: 280px;
  width: 100%;
}

/* 快捷操作 */
.quick-actions {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 16px;
  background: #f8fafc;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(30, 58, 95, 0.2);
}

.action-card:hover span,
.action-card:hover .action-icon {
  color: white;
}

.action-icon {
  width: 48px;
  height: 48px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #1e3a5f;
  box-shadow: 0 2px 8px rgba(30, 58, 95, 0.1);
  transition: all 0.3s;
}

.action-card span {
  font-size: 14px;
  font-weight: 600;
  color: #5a7a9a;
  transition: all 0.3s;
}

/* 内容卡片 */
.content-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
  margin-bottom: 20px;
}

.overtime-card {
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
  border: 1px solid #fcd34d;
}

.overtime-card .card-header h3 {
  color: #92400e;
}

.card-tip {
  font-size: 13px;
  color: #92400e;
  margin: 4px 0 0;
  font-weight: normal;
}

.overtime-fee-text {
  font-weight: 600;
  color: #d97706;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 表单 */
.staff-form {
  max-width: 480px;
}

.scooter-status-note {
  font-size: 12px;
  color: #6b9ac4;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.scooter-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 2px 0;
}

.scooter-name {
  font-weight: 600;
}

.scooter-status {
  font-size: 12px;
  padding: 1px 6px;
  border-radius: 4px;
}

.scooter-status.available {
  background: #dcfce7;
  color: #166534;
}

.scooter-status.in-use {
  background: #fef3c7;
  color: #92400e;
}

.scooter-status.maintenance {
  background: #f1f5f9;
  color: #475569;
}

.scooter-battery {
  font-size: 12px;
  color: #64748b;
}

.selected-scooter-info {
  margin-top: 8px;
  font-size: 13px;
  color: #1e3a5f;
  font-weight: 600;
  padding: 8px 12px;
  background: #f0f7ff;
  border-radius: 6px;
  border: 1px solid #dbeafe;
}

/* 电池样式 */
.battery-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.action-buttons {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

/* 运营指标网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding: 8px 0;
}

.metric-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.metric-label {
  font-size: 13px;
  color: #94a3b8;
}

.metric-value {
  font-size: 20px;
  font-weight: 700;
  color: #1e3a5f;
}

/* 预警列表 */
.alert-list {
  max-height: 280px;
  overflow-y: auto;
}

.no-alert {
  text-align: center;
  padding: 40px;
  color: #94a3b8;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 8px;
  font-size: 13px;
}

.alert-item.danger {
  background: #fde8e8;
  color: #d14545;
}

.alert-item.warning {
  background: #fef7e6;
  color: #c4880c;
}

.alert-item.info {
  background: #e8f4fd;
  color: #3b5998;
}

/* 统计子项 */
.stat-sub {
  display: flex;
  gap: 12px;
  margin-top: 4px;
  font-size: 12px;
  color: #94a3b8;
}

/* 地图样式 */
.map-card {
  padding: 0;
}

.map-card .card-header {
  padding: 20px 24px;
}

.map-container {
  position: relative;
}

.admin-map {
  height: 450px;
  background: #f0f4f8;
  border-radius: 0 0 16px 16px;
}

.simple-map {
  width: 100%;
  height: 100%;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  position: relative;
}

.map-placeholder p {
  margin: 8px 0 0;
}

.map-hint {
  font-size: 13px;
  color: #6b9ac4;
}

.map-markers {
  position: absolute;
  width: 100%;
  height: 80%;
  top: 10%;
}

.marker {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 2px 6px rgba(0,0,0,0.3);
}

.marker:hover {
  transform: translate(-50%, -50%) scale(1.4);
}

.map-legend {
  position: absolute;
  top: 16px;
  right: 16px;
  background: white;
  padding: 12px 16px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.legend-dot.available { background: #2d8a4e; }
.legend-dot.inuse { background: #c4880c; }
.legend-dot.maintenance { background: #94a3b8; }

.map-stats {
  position: absolute;
  bottom: 16px;
  left: 16px;
  background: white;
  padding: 12px 20px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  display: flex;
  gap: 24px;
}

.map-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.map-stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e3a5f;
}

.map-stat-label {
  font-size: 12px;
  color: #94a3b8;
}

/* 用户排名 */
.user-ranking {
  max-height: 280px;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f4f8;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-num {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #f0f4f8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 13px;
  color: #5a7a9a;
}

.ranking-num.gold { background: linear-gradient(135deg, #ffd700, #ffb347); color: #8b6914; }
.ranking-num.silver { background: linear-gradient(135deg, #c0c0c0, #a8a8a8); color: #5a5a5a; }
.ranking-num.bronze { background: linear-gradient(135deg, #cd7f32, #b87333); color: #5a3a1a; }

.ranking-name {
  flex: 1;
  font-weight: 500;
  color: #1e3a5f;
}

.ranking-value {
  font-weight: 600;
  color: #6b9ac4;
}

.action-buttons .el-button {
  margin: 2px 0;
}

.battery-val {
  font-size: 13px;
  font-weight: 600;
}

.battery-bar {
  width: 60px;
  height: 6px;
  background: #e8eef5;
  border-radius: 3px;
  overflow: hidden;
}

.battery-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s;
}

.battery-fill.high { background: #2d8a4e; }
.battery-fill.medium { background: #c4880c; }
.battery-fill.low { background: #d14545; }

/* 库存徽章 */
.stock-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.stock-badge.good { background: #e6f4ea; color: #2d8a4e; }
.stock-badge.warning { background: #fef7e6; color: #c4880c; }
.stock-badge.danger { background: #fde8e8; color: #d14545; }

/* ID14: 高优先级警示框 */
.high-priority-alert {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #fde8e8 0%, #fef2f2 100%);
  border: 1px solid #f5c6c6;
  border-radius: 10px;
  margin-bottom: 16px;
  color: #d14545;
  font-size: 14px;
}

.high-priority-alert .el-icon {
  font-size: 18px;
}

.high-priority-alert strong {
  color: #d14545;
  font-weight: 700;
}

/* 弹窗 */
.process-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #e8eef5;
  padding-bottom: 16px;
}

.process-dialog :deep(.el-dialog__title) {
  font-weight: 700;
  color: #1e3a5f;
}

/* ID16: 电量配置弹窗样式 */
.battery-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #e8eef5;
  padding-bottom: 16px;
}

.battery-dialog :deep(.el-dialog__title) {
  font-weight: 700;
  color: #1e3a5f;
}

.battery-scooter-number {
  font-weight: 700;
  font-size: 16px;
  color: #1e3a5f;
}

.battery-current {
  display: flex;
  align-items: center;
  gap: 12px;
}

.battery-bar-large {
  flex: 1;
  height: 20px;
  background: #e8eef5;
  border-radius: 10px;
  overflow: hidden;
}

.battery-fill-large {
  height: 100%;
  border-radius: 10px;
  transition: width 0.3s;
}

.battery-fill-large.high { background: linear-gradient(90deg, #2d8a4e, #5cb885); }
.battery-fill-large.medium { background: linear-gradient(90deg, #c4880c, #e6a82d); }
.battery-fill-large.low { background: linear-gradient(90deg, #d14545, #ef4444); }

.battery-percentage {
  font-weight: 700;
  font-size: 18px;
  color: #1e3a5f;
  min-width: 50px;
  text-align: right;
}

.battery-presets {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  flex-wrap: wrap;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .actions-grid { grid-template-columns: repeat(2, 1fr); }
  .overview-row, .chart-row { grid-template-columns: 1fr; }
  .metrics-grid { grid-template-columns: repeat(2, 1fr); }
}

/* ============================================
   移动端响应式设计
   ============================================ */

/* 平板及以下 - 隐藏侧边栏，显示移动端顶部栏 */
@media (max-width: 900px) {
  .admin-container {
    padding-top: 60px;
  }

  .sidebar {
    display: none;
  }

  .mobile-header {
    display: flex;
  }

  .main-area {
    margin-left: 0;
    padding: 16px;
  }

  .content-wrapper {
    padding: 0;
  }

  /* 表格横向滚动 */
  .content-card .el-table {
    overflow-x: auto;
  }

  .content-card .el-table__body-wrapper {
    overflow-x: auto;
  }

  /* 表格横向滚动容器 */
  .tab-content .el-table {
    display: block;
    overflow-x: scroll;
    -webkit-overflow-scrolling: touch;
  }

  /* 小型统计卡片网格 - 移动端自适应 */
  .stats-grid.mini {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .stat-mini {
    padding: 12px 16px;
  }

  .mini-value {
    font-size: 24px;
  }

  /* 头部操作按钮响应式 */
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .header-actions .el-input {
    width: 100% !important;
  }

  .header-actions .el-select {
    width: 100% !important;
  }

  .header-actions .el-button {
    width: 100%;
  }
}

/* 移动端顶部栏 */
.mobile-header {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: white;
  border-bottom: 1px solid #d6e0eb;
  padding: 0 16px;
  align-items: center;
  justify-content: space-between;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.mobile-header .mobile-title {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.mobile-header .brand-name {
  font-size: 14px;
  font-weight: 800;
  color: #1e3a5f;
}

.mobile-header .brand-tag {
  font-size: 10px;
  color: #5a7a9a;
}

.mobile-header .mobile-avatar {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  color: white;
  font-weight: 700;
}

.mobile-menu-btn {
  width: 40px;
  height: 40px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f4f8;
  border: 1px solid #d6e0eb;
  border-radius: 8px;
  color: #1e3a5f;
}

/* 移动端抽屉样式 */
.mobile-drawer :deep(.el-drawer__body) {
  padding: 0;
}

.drawer-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.drawer-header {
  padding: 20px;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
}

.drawer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.drawer-logo .logo-icon {
  width: 40px;
  height: 40px;
  background: rgba(255,255,255,0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.drawer-logo .logo-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.drawer-logo .logo-text {
  display: flex;
  flex-direction: column;
}

.drawer-logo .brand-name {
  font-size: 16px;
  font-weight: 800;
  color: white;
}

.drawer-logo .brand-tag {
  font-size: 11px;
  color: rgba(255,255,255,0.7);
}

.drawer-nav {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.drawer-nav .nav-section {
  margin-bottom: 16px;
}

.drawer-nav .nav-label {
  padding: 0 12px;
  margin-bottom: 6px;
}

.drawer-nav .nav-item {
  padding: 12px;
  font-size: 14px;
}

.drawer-nav .badge {
  font-size: 10px;
  padding: 2px 6px;
}

.drawer-footer {
  padding: 16px;
  border-top: 1px solid #e8eef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.drawer-footer .admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.drawer-footer .admin-avatar {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  color: white;
}

.drawer-footer .admin-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e3a5f;
}

.drawer-footer .admin-role {
  font-size: 11px;
  color: #5a7a9a;
}

/* 手机小屏幕 - 更多调整 */
@media (max-width: 600px) {
  .admin-container {
    padding-top: 56px;
  }

  .mobile-header {
    height: 56px;
    padding: 0 12px;
  }

  .main-area {
    padding: 12px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid.mini {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .actions-grid {
    grid-template-columns: 1fr;
  }

  .stat-card {
    padding: 16px;
  }

  .stat-value {
    font-size: 24px;
  }

  /* 表格响应式 */
  .content-card {
    padding: 16px;
  }

  .el-table {
    font-size: 12px;
  }

  .el-table .cell {
    padding: 8px 4px;
  }

  /* 操作按钮在小屏上堆叠 */
  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
    margin: 2px 0;
  }

  /* 弹窗响应式 */
  .process-dialog {
    width: 95% !important;
    max-width: 95%;
  }

  .process-dialog .el-dialog__body {
    padding: 16px;
  }

  /* 表单响应式 */
  .staff-form {
    max-width: 100%;
  }

  .staff-form .el-form-item {
    margin-bottom: 16px;
  }

  /* 图表响应式 */
  .chart {
    height: 220px;
  }

  /* 运营指标响应式 */
  .metrics-grid {
    grid-template-columns: 1fr;
  }

  /* 快捷操作响应式 */
  .action-card {
    padding: 16px;
  }

  /* 排名列表响应式 */
  .user-ranking {
    max-height: 200px;
  }

  /* 预警列表响应式 */
  .alert-list {
    max-height: 200px;
  }

  /* 地图响应式 */
  .admin-map {
    height: 300px;
  }

  /* 价格配置表格 */
  .el-input-number {
    width: 80px !important;
  }
}

/* 超小屏幕 */
@media (max-width: 380px) {
  .mobile-header .brand-name {
    font-size: 12px;
  }

  .mobile-menu-btn {
    width: 36px;
    height: 36px;
  }

  .stats-grid.mini {
    grid-template-columns: 1fr;
  }

  .mini-value {
    font-size: 20px;
  }

  /* 弹窗进一步适配 */
  .process-dialog {
    width: 100% !important;
    max-width: 100%;
    margin: 0 !important;
  }

  .process-dialog .el-dialog {
    width: 100% !important;
    max-width: 100%;
    margin: 0 !important;
    border-radius: 16px 16px 0 0;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
  }
}

/* 平板横屏适配 */
@media (min-width: 601px) and (max-width: 900px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stats-grid.mini {
    grid-template-columns: repeat(4, 1fr);
  }

  .el-input-number {
    width: 100px !important;
  }
}

/* 表格单元格内容折行 */
.el-table .cell {
  word-break: break-word;
}

/* 固定列在移动端隐藏部分操作 */
@media (max-width: 768px) {
  .el-table__fixed {
    display: none;
  }

  .el-table__fixed-right {
    display: none;
  }
}
</style>
