import {
    hostServer,
    formatDate,
    generateUUIDv4,
    unauthorizedRedirect,
    fetchPOST,
    fetchGET,
    getClientInfo,
    isValidPhoneNumber,
    isValidAlphabetic,
    isValidEmail,
    isValidDate,
    fetchPOSTFull,
    fetchGETFull
} from "./common.js";

export default {
    template: `
      <div class="d-sm-flex align-items-center justify-content-between mb-4">
         <h1 class="h3 mb-0 text-gray-800">Management Role - Menu</h1>
         <a
            href="#"
            class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm"
            data-toggle="modal"
            data-target="#registerNewUserModal"
            @click="resetFormInputUserRegistration(false)"
            ><i class="fas fa-user-plus"></i> Create Menu</a
         >
      </div>
      <hr class="border-top-success">

    <div class="card shadow mb-4">
      <div class="card-header py-3">
        <ul class="nav nav-tabs card-header-tabs">
          <li class="nav-item">
            <a
              href="#"
              class="nav-link"
              :class="{ active: activeTab === 'role' }"
              @click.prevent="changeTab('role')"
            >
              <i class="fas fa-user-shield mr-1"></i> Role
            </a>
          </li>

          <li class="nav-item">
            <a
              href="#"
              class="nav-link"
              :class="{ active: activeTab === 'menu' }"
              @click.prevent="changeTab('menu')"
            >
              <i class="fas fa-list mr-1"></i> Menu
            </a>
          </li>
        </ul>
      </div>

     <div class="tab-content mt-3">
       <div v-if="activeTab === 'role'">
         <roleTab />
       </div>

       <div v-if="activeTab === 'menu'">
         <menuTab />
       </div>
     </div>


      <div
         class="modal fade"
         id="registerNewUserModal"
         tabindex="-1"
         role="dialog"
         aria-labelledby="registerModalLabel"
         aria-hidden="true"
      >
         <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
               <!-- Modal Header -->
               <div class="modal-header">
                  <h5 class="modal-title" id="registerModalLabel">Create New User</h5>
                  <button type="button" class="close" data-dismiss="modal">
                     <span>&times;</span>
                  </button>
               </div>

               <!-- Modal Footer -->

            </div>
         </div>
      </div>
  `,
    components: {
        /*  roleTab: () => import('./v-management-role-tab.js'),
          menuTab: () => import('./v-management-menu-tab.js'),*/
          roleTab: Vue.defineAsyncComponent(() =>
                import('./v-management-role-tab.js')
              ),
          menuTab: Vue.defineAsyncComponent(() =>
                import('./v-management-menu-tab.js')
              )
      },
    data() {
        return {
             activeTab: 'role',
        };
    },
    async mounted() {

    },
    computed: {
        //dropdown search text
        filteredList() {
            return this.items.filter(item =>
                item.name.toLowerCase().includes(this.vmodalRoleName.toLowerCase())
            );
        }
    },
    watch: {
        // 🔁 sync URL → tab
         '$route.query.tab': {
              immediate: true,
              handler(val) {
                this.activeTab = val || 'role';
              }
            },

    },
    methods: {
    changeTab(tab) {
                  this.$router.push({
                    query: { tab }
                  });
                }

    }
};