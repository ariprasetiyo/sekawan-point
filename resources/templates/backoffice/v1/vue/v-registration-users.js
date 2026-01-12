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
    fetchPOSTFull,
    fetchGETFull
} from "./common.js";

export default {
    template: `
      <div class="d-sm-flex align-items-center justify-content-between mb-4">
         <h1 class="h3 mb-0 text-gray-800">User Registration</h1>
         <a
            href="#"
            class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm"
            data-toggle="modal"
            data-target="#registerNewUserModal"
            ><i class="fas fa-user-plus"></i> Create user</a
         >
      </div>

      <div class="card o-hidden border-0 shadow-lg my-3">
         <div class="card-body p-0">
            <!-- Nested Row within Card Body -->
            <div class="card-body">
               <div class="table-responsive">
                  <table
                     class="table table-bordered"
                     id="dataTable"
                     width="100%"
                     cellspacing="0"
                  >
                     <thead>
                        <tr>
                           <th>User Id</th>
                           <th>Username</th>
                           <th>Phone Number</th>
                           <th>Email</th>
                           <th>Role Id</th>
                           <th>Is Active</th>
                           <th>Created</th>
                           <th>Updated</th>
                           <th>action</th>
                        </tr>
                     </thead>
                     <tfoot>
                        <tr>
                           <th>User Id</th>
                           <th>Username</th>
                           <th>Phone Number</th>
                           <th>Email</th>
                           <th>Role Id</th>
                           <th>Is Active</th>
                           <th>Created</th>
                           <th>Updated</th>
                           <th>action</th>
                        </tr>
                     </tfoot>
                     <tbody>
                        <tr v-for="user in listOfUser" :key="user.id">
                           <td>{{ user.userId }}</td>
                           <td>{{ user.username }}</td>
                           <td>{{ user.phoneNumber }}</td>
                           <td>{{ user.email }}</td>
                           <td>{{ user.roleId }}</td>
                           <td>{{ user.isActive }}</td>
                           <td>{{ user.createdAt }}</td>
                           <td>{{ user.updatedAt }}</td>
                           <td><a class="btn btn-primary btn-user" id="{{user.userId}}" >edit</a> <button class="btn btn-primary btn-user"  @click="submitDeleteUser(user.userId, user.username)" >delete</button></td>
                        </tr>
                     </tbody>
                  </table>
               </div>
            </div>
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

               <!-- Modal Body -->
               <div class="modal-body">
                  <form class="user" id="registerForm">
                     <div class="form-group row">
                        <div class="col-sm-6 mb-3 mb-sm-0">
                           <input
                              type="text"
                              class="form-control form-control-user"
                              id="exampleFirstName"
                              v-model="vModalFistName"
                              :class="{'is-invalid': !firstNameValid, 'is-valid': firstNameValid}"
                              placeholder="First Name"
                           />
                        </div>
                        <div class="col-sm-6">
                           <input
                              type="text"
                              class="form-control form-control-user"
                              id="exampleLastName"
                              v-model="vModalLastName"
                              placeholder="Last Name"
                           />
                        </div>
                     </div>
                     <div class="form-group">
                        <input
                           type="email"
                           class="form-control form-control-user"
                           id="exampleInputEmail"
                           v-model="vModalEmail"
                           :class="{'is-invalid': !emailValid, 'is-valid': emailValid}"
                           placeholder="Email Address"
                        />
                     </div>
                     <div class="form-group">
                        <input
                           type="text"
                           class="form-control form-control-user"
                           id="inputPhoneNumber"
                           placeholder="Phone Number"
                           ref="refInputPhoneNumber"
                           v-model="vmodalInputPhoneNumber"
                           :class="{'is-invalid': !phoneValid, 'is-valid': phoneValid}"
                        />
                     </div>
                     <div class="form-group row">
                        <div class="col-sm-6 mb-3 mb-sm-0">
                           <input
                              type="password"
                              class="form-control form-control-user"
                              id="inputPassword"
                              placeholder="Password"
                              ref="refInputPassword"
                              v-model="vmodalInputPassword"
                              :class="{'is-invalid': !passwordValid, 'is-valid': passwordValid}"
                           />
                        </div>
                        <div class="col-sm-6">
                           <input
                              type="password"
                              class="form-control form-control-user"
                              id="exampleRepeatPassword"
                              placeholder="Repeat Password"
                              :class="{'is-invalid': !passwordRepeatValid, 'is-valid': passwordRepeatValid}"
                              v-model="vmodalInputPasswordRepeat"
                           />
                        </div>
                     </div>
                     <!-- dropdown search text -->
                     <div class="form-group">
                        <div class="dropdown">
                           <input
                              type="text"
                              class="form-control dropdown-toggle form-control-user"
                              placeholder="Search role id ..."
                              v-model="vmodalRoleName"
                              @focus="showDropdown = true"
                              @input="filterList"
                           />
                           <div class="dropdown-menu show w-100" v-if="showDropdown">
                              <a
                                 class="dropdown-item"
                                 v-for="item in filteredList"
                                 :key="item.id"
                                 @click="selectItem(item)"
                              >
                                 {{ item.name }}
                              </a>
                              <div
                                 v-if="filteredList.length === 0"
                                 class="dropdown-item text-muted"
                              >
                                 No result found
                              </div>
                           </div>
                        </div>
                     </div>
                     <!-- dropdown search text end -->
                  </form>
               </div>

               <!-- Modal Footer -->
               <div class="modal-footer">
                <div v-if="vResponseStatusRegistrationUser != null" class="card bg-success text-white shadow">
                  <div class="card-body">
                    Registration {{ vResponseStatusRegistrationUser }} !
                  </div>
                </div>
                  <a class="btn btn-secondary btn-user"  @click="resetFormInputUserRegistration(true)">
                     Cancel</a
                  >
                  <a class="btn btn-primary btn-user" @click="submitSaveUserForm">
                     Register Account</a
                  >
               </div>
            </div>
         </div>
      </div>
  `,
    data() {
        return {
            vResponseStatusRegistrationUser: null,
            listOfUser: [],

            vModalFistName: "",
            firstNameValid: false,
            vModalEmail: "",
            emailValid: false,
            vModalLastName: "",
            vmodalInputPhoneNumber: "+62",
            vmodalInputPassword: "",
            vmodalInputPasswordRepeat: null,
            passwordValid: false,
            phoneValid: false,
            passwordRepeatValid: false,
            //dropdown search text
            vmodalRoleName: "",
            vmodalRoleId: null,

            showDropdown: false,
            selectedItem: null,
            filterList: null,
            items: []
            //dropdown search text end
        };
    },
    async mounted() {

        this.listOfUser = await this.loadListOfUser();
        this.$nextTick(() => {
            //            $('#dataTable').DataTable();

            if ($.fn.DataTable.isDataTable('#dataTable')) {
                $('#dataTable').DataTable().destroy();
            }

            $('#dataTable').DataTable({
                responsive: true,
                pageLength: 25,
                language: {
                    search: "_INPUT_",
                    searchPlaceholder: "Search..."
                }
            });
        });

        this.loadUsers(); // fetch data on load
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
        vmodalInputPassword(value) {
            if (value.length >= 8) {
                this.passwordValid = true;
            } else {
                this.passwordValid = false;
            }
        },
        vmodalInputPasswordRepeat(value) {
            if (value == this.vmodalInputPassword) {
                this.passwordRepeatValid = true
            } else {
                this.passwordRepeatValid = false
            }
        },
        vmodalInputPhoneNumber(value) {
            this.phoneValid = isValidPhoneNumber(value)
        },
        vModalFistName(value) {
            this.firstNameValid = isValidAlphabetic(value)
        },
        vModalEmail(value) {
            this.emailValid = isValidEmail(value)
        }

    },
    methods: {
        resetFormInputUserRegistration(isHideShowForm) {
            if(isHideShowForm){
                $('#registerNewUserModal').modal('hide');
            }
            this.vModalFistName = "",
            this.firstNameValid = false;
            this.vModalEmail = "";
            this.emailValid = false;
            this.vModalLastName = "";
            this.vmodalInputPhoneNumber = "+62";
            this.vmodalInputPassword = "";
            this.vmodalInputPasswordRepeat = null;
            this.passwordValid = false;
            this.phoneValid = false;
            this.passwordRepeatValid = false;
            //dropdown search text
            this.vmodalRoleName = "";
            this.vmodalRoleId = null;
            this.vResponseStatusRegistrationUser = null
        },
        buildCreateUserJson(uuid) {
            return {
                requestId: uuid,
                type: "registration_user",
                body: {
                    username: this.vModalFistName.trim() + " " + this.vModalLastName.trim(),
                    password: this.vmodalInputPassword,
                    email: this.vModalEmail.trim().toLowerCase(),
                    phoneNumber: this.vmodalInputPhoneNumber,
                    roleId: this.vmodalRoleId,
                    isActive: true
                }
            };
        },
        buildDeleteUserJson(uuid, userId, username) {
            return {
                requestId: uuid,
                type: "delete_user",
                body: {
                    userId: userId,
                    username: username
                }
            };
        },
        getValue() {
            alert(this.$refs.refInputPhoneNumber.value);
        },
        showValue() {
            alert(this.vmodalInputPhoneNumber);
        },
        async submitSaveUserForm() {
            if (!this.phoneValid) {
                alert("Nomor HP tidak valid!");
                return;
            }
            var clientInfo = getClientInfo();
            const requestJson = this.buildCreateUserJson(clientInfo.uuid)

            const responseSaveUser = await fetchPOSTFull("/api/v1/registration/user/save", clientInfo, JSON.stringify(requestJson));
            console.info(responseSaveUser);
            this.vResponseStatusRegistrationUser = responseSaveUser.statusMessage
            if (responseSaveUser.status == 100) {
                this.listOfUser = await this.loadListOfUser();
            }
            this.resetFormInputUserRegistration(true);

        },
        async submitDeleteUser(userId, username) {

            const resultConfirmation = await Swal.fire({
              title: "Are you sure delete this user "+ username+" ?",
              text: "This action cannot be undone",
              icon: "warning",
              showCancelButton: true,
              confirmButtonText: "Yes, submit"
            });

           if(!resultConfirmation.isConfirmed){
                return;
           }

            var clientInfo = getClientInfo();
            const requestJson = this.buildDeleteUserJson(clientInfo.uuid, userId, username)
            const responseDeleteUser = await fetchPOSTFull("/api/v1/registration/user/delete", clientInfo, JSON.stringify(requestJson));
            this.vResponseStatusRegistrationUser = responseDeleteUser.statusMessage
            if (responseDeleteUser.status == 100) {
                this.listOfUser = await this.loadListOfUser();
            }

        },
        //dropdown search text
        selectItem(item) {
            this.vmodalRoleId = item.id;
            this.vmodalRoleName = item.name
            this.selectedItem = item;
            this.showDropdown = false;
        },
        async loadListOfUser() {

            var clientInfo = getClientInfo()
            const requestJson = {
                requestId: clientInfo.uuid,
                type: "users",
                body: {}
            };

            // Deserialize here
            const dataJson = await fetchPOSTFull("/api/v1/registration/user/list", clientInfo, JSON.stringify(requestJson));
            return dataJson.body.list.map(item => ({
                userId: item.userId,
                username: item.username,
                passwordHash: item.passwordHash,
                email: item.email,
                roleId: item.roleId,
                isActive: item.isActive,
                phoneNumber: item.phoneNumber,
                createdAt: item.createdAt,
                updatedAt: item.updatedAt
            }));
        },
        async loadUsers() {

            var clientInfo = getClientInfo()
            var responseRoleList = await fetchGET("/api/v1/registration/role/list", clientInfo);
            unauthorizedRedirect(responseRoleList);
            // Deserialize here
            const dataJson = await responseRoleList.json();
            this.items = dataJson.body.list.map(item => ({
                id: item.id,
                name: item.name,
                description: item.description,
                active: item.isActive,
                createdAt: new Date(item.createdAt),
                updatedAt: new Date(item.updatedAt)
            }));
        },
    }
};