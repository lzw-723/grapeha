import { createRouter, createWebHashHistory } from "vue-router";
import { fetchBookById } from "./api";
import MainView from "./views/MainView.vue";
import LoginView from "./views/LoginView.vue";
import BookView from "./views/BookView.vue";

// 1. 定义路由组件.
// 也可以从其他文件导入

// 2. 定义一些路由
// 每个路由都需要映射到一个组件。
// 我们后面再讨论嵌套路由。
const routes = [
  { path: "/", component: MainView },
  {
    path: "/books/:id",
    component: BookView,
  },
  { path: "/login", component: LoginView },
];

// 3. 创建路由实例并传递 `routes` 配置
// 你可以在这里输入更多的配置，但我们在这里
// 暂时保持简单
const router = createRouter({
  // 4. 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
  history: createWebHashHistory(),
  routes, // `routes: routes` 的缩写
});

export { router };

// 5. 创建并挂载根实例
