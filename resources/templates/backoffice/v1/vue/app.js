const { createApp } = Vue;
const { createRouter, createWebHashHistory } = VueRouter;

// Components (HTML + JS)
import MainJs from "./v-main.js";
import Dashboard from "./v-dashboard.js";
import RegistrationUsers from "./v-registration-users.js";

const routes = [
  //main home
  { path: "/", component: MainJs },
  { path: "/nav-main", component: MainJs },
  { path: "/nav-dashboard", component: Dashboard },
  { path: "/nav-registration", component: RegistrationUsers }
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
