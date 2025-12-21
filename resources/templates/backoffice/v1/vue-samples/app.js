const { createApp } = Vue;
const { createRouter, createWebHashHistory } = VueRouter;

// Components (HTML + JS)
import Home from "./v-home.js";
import Dashboard from "./v-dashboard.js";

const routes = [
  //main home
  { path: "/", component: Home },
  { path: "/navigate-dashboard", component: Dashboard }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

// ❗ ROOT COMPONENT MUST HAVE router-view
const App = {
  template: '<router-view></router-view>'
};

createApp(App)
  .use(router)
  .mount("#app-main");
