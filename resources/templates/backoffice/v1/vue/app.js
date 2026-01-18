const { createApp } = Vue;
const { createRouter, createWebHashHistory } = VueRouter;

// Components (HTML + JS)
import MainJs from "./v-main.js";
import Dashboard from "./v-dashboard.js";
import RegistrationUsers from "./v-registration-users.js";
import managementRoleAndMenu from "./v-management-role-and-menu.js";

const routes = [
  //main home
  { path: "/", component: MainJs },
  { path: "/nav-main", component: MainJs },
  { path: "/nav-dashboard", component: Dashboard },
  { path: "/nav-registration", component: RegistrationUsers },
  { path: "/nav-management-role-and-menu", component: managementRoleAndMenu }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

let loadingStartTime = 0;
const MIN_LOADING_TIME = 300; // ms

const loadingState = Vue.reactive({
  loading: false,
});

const App = {
  setup() {
    return { loadingState };
  },
  template: `<div>
                <div v-if="loadingState.loading" class="page-loader">
                Loading...
                </div>
                <router-view></router-view>'
             </div>
             `
};

// 🔵 BEFORE route loads
router.beforeEach((to, from, next) => {
  loadingStartTime = Date.now();
  loadingState.loading = true;
  next();
});

// 🟢 AFTER route finished
router.afterEach(() => {
  const elapsed = Date.now() - loadingStartTime;
  const remaining = Math.max(MIN_LOADING_TIME - elapsed, 0);

  setTimeout(() => {
    loadingState.loading = false;
  }, remaining); // small delay for UX
});

// ❗ ROOT COMPONENT MUST HAVE router-view
createApp(App)
  .use(router)
  .mount("#app-main");
