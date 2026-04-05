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
      <div class="d-sm-flex align-items-center justify-content-between mb-1">
         <h1 class="h3 mb-0 text-gray-800">User Registration</h1>
         <a
            href="#"
            class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm"
            data-toggle="modal"
            data-target="#registerNewUserModal"
            @click="resetFormInputUserRegistration(false)"
            ><i class="fas fa-user-plus"></i> Create user</a
         >
      </div>

      <div class="card o-hidden border-0 shadow-lg my-3">
         <div class="card-body p-0">
            <!-- Nested Row within Card Body -->
            <div class="card-body">
               <!-- <div class="table-responsive"> -->
               <div class="table-responsive">
                    <div class="d-flex  justify-content-between ">
                        <!-- Show entries -->
                        <div id="datatable-default-length"></div>
                        <!-- LEFT GROUP -->
                        <div class="d-flex align-items-center gap-2">
                            <!-- Role dropdown -->
                            <div id="filter-column-container" style="min-width:200px;">
                            <select id="datatable-default-search-type" class="form-control">
                              <option value="all">All</option>
                              <option value="user_id">User id</option>
                              <option value="username">Username</option>
                              <option value="emai">Email</option>
                              <option value="role_id">Role id</option>
                              <option value="is_active">Is Active</option>
                            </select>
                            </div>
                            <!-- RIGHT SEARCH -->
                            <div class="input-group">
                                <input id="datatable-default-search-input" type="text" class="form-control bg-light border-0 small" placeholder="Search for..." aria-label="Search" aria-describedby="basic-addon2">
                                <div class="input-group-append">
                                    <button class="btn btn-primary" type="button" id="datatable-default-search-button">
                                        <i class="fas fa-search fa-sm"></i>
                                    </button>
                                </div>
                            </div>
<!--                            <div id="datatable-default-search"></div>-->
<!--                            <button id="datatable-default-search-button" class="btn btn-primary btn-sm edit-btn">Submit</button>-->
                        </div>
                    </div>
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
                              placeholder="First Name"/>
                            </div>
                            <div class="col-sm-6">
                           <input
                              type="text"
                              class="form-control form-control-user text-center"
                              v-model="vModalUserId"/>
                           </div>
                     </div>
                     <div class="form-group row">
                         <div class="col-sm-6 mb-3 mb-sm-0">
                            <input
                               type="text"
                               class="form-control form-control-user"
                               placeholder="Birth Place"
                               v-model="vModalBirthPlace"
                               :class="{'is-invalid': !isValidBirthPlace, 'is-valid': isValidBirthPlace}"
                            />
                         </div>
                         <div class="col-sm-6">
                            <input
                               type="text"
                               class="form-control form-control-user"
                               id="calendarInput"
                               placeholder="Birth date"
                               :class="{'is-invalid': !isBirthDateValid, 'is-valid': isBirthDateValid}"
                               v-model="vModalBirthDate"
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
            vModalBirthPlace: null,
            isValidBirthPlace: false,
            vModalBirthDate: null,
            isBirthDateValid: false,
            vModalUserId: null,
            vModalFistName: "",
            firstNameValid: false,
            vModalEmail: "",
            emailValid: false,
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
            items: [],
            vmInternal : null,
            //dropdown search text end
        };
    },
    async mounted() {

        flatpickr("#calendarInput", {
            dateFormat: "Y-m-d"
        });
        // this.listOfUser = await this.loadListOfUser();
        const vm = this; // 🔥 simpan Vue instance
        this.vmInternal = vm
        this.$nextTick(() => {
            //            $('#dataTable').DataTable();

            if ($.fn.DataTable.isDataTable('#dataTable')) {
                $('#dataTable').DataTable().destroy();
            }

            $('#datatable-default-search-type').on('change', function() {
                table.draw();
            });

            this.vmInternal.table  = $('#dataTable').DataTable({
                initComplete: function() {
                    const api = this.api();
                    // $('#dataTable_filter').appendTo('#datatable-default-search');
                    $('#dataTable_length').appendTo('#datatable-default-length');
                    $('#dataTable_filter input').off(); // 🔥 hapus auto trigger search

                },
                processing: true,
                serverSide: true,
                paging: true,
                searching: false,
                scrollX: true,
                scrollY: '500px',
                pageLength: 10,
                deferLoading: 0, // 🔥 IMPORTANT → tidak load saat init

                ajax: async (data, callback) => {

                    //use this if every event key up will trigger
                    // const search = data.search.value;
                    const searchInput = $('#datatable-default-search-input').val();
                    const searchType = $('#datatable-default-search-type').val();
                    const page = (data.start / data.length) + 1;
                    const size = data.length;

                    const clientInfo = getClientInfo();

                    const requestJson = {
                        requestId: clientInfo.uuid,
                        type: "users",
                        body: {
                            page: page,
                            size: size,
                            searchText: searchInput,
                            searchType: searchType
                        }
                    };

                    const res = await fetchPOSTFull(
                        "/api/v2/registration/user/list",
                        clientInfo,
                        JSON.stringify(requestJson)
                    );

                    //use this after save / delete $('#dataTable').DataTable().ajax.reload();

                    //Showing 1 to 10 of 60 entries (filtered from 120 total entries) -> recordsTotal: 120, recordsFiltered: 60,
                    //Showing 1 to 10 of 60 entries -> recordsTotal: 60,  recordsFiltered: 60,
                    callback({
                        draw: data.draw,
                        recordsTotal: res.totalRecords,
                        recordsFiltered: res.totalRecords,
                        data: res.body.list
                    });
                },

                columns: [
                    { data: "userId" },
                    { data: "username" },
                    { data: "phoneNumber" },
                    { data: "email" },
                    { data: "roleId" },
                    { data: "isActive" },
                    { data: "createdAt" },
                    { data: "updatedAt" },
                    {
                        data: null,
                        render: (data) => {
                            return `
                  <button class="btn btn-primary btn-sm edit-btn" data-id="${data.userId}" data-name="${data.username}">edit</button>
                  <button class="btn btn-danger btn-sm delete-btn" data-id="${data.userId}" data-name="${data.username}">delete</button>
                `;
                        }
                    }
                ]
            });

        });

        $('#dataTable tbody').on('click', '.edit-btn', (e) => {
            const userId = e.target.dataset.id;
            const username = e.target.dataset.name;
            this.submitEditUser(userId, username);
        });

        $('#dataTable tbody').on('click', '.delete-btn', (e) => {
            const userId = e.target.dataset.id;
            const username = e.target.dataset.name;
            this.submitDeleteUser(userId, username);
        });

        $(document).on('click', '#datatable-default-search-button', function () {
            if (!vm.table) {
                console.error("DataTable not initialized yet");
                return;
            }
            vm.table.ajax.reload(null, true);
        });

        // fetch data on load
        this.loadUsers();
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
        },
        vModalBirthDate(value) {
            this.isBirthDateValid = isValidDate(value)
        },
        vModalBirthPlace(value) {
            this.isValidBirthPlace = isValidAlphabetic(value)
        }
    },
    methods: {
        resetFormInputUserRegistration(isHideShowForm) {
            if (isHideShowForm) {
                $('#registerNewUserModal').modal('hide');
            }
            this.vModalUserId = null,
                this.vModalFistName = "",
                this.firstNameValid = false;
            this.vModalBirthPlace = null;
            this.isValidBirthPlace = false;
            this.vModalBirthDate = null;
            this.isBirthDateValid = false;
            this.vModalEmail = "";
            this.emailValid = false;
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
                    userId: this.vModalUserId,
                    username: this.vModalFistName.trim(),
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
        buildGetUserJson(uuid, userId, username) {
            return {
                requestId: uuid,
                type: "users",
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
                title: "Are you sure delete this user " + username + " ?",
                text: "This action cannot be undone",
                icon: "warning",
                showCancelButton: true,
                confirmButtonText: "Yes, submit"
            });

            if (!resultConfirmation.isConfirmed) {
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
        async submitEditUser(userId, username) {

            var clientInfo = getClientInfo()
            const requestJson = this.buildGetUserJson(clientInfo.uuid, userId, username)
            const dataJson = await fetchPOSTFull("/api/v1/registration/user/detail", clientInfo, JSON.stringify(requestJson));

            $('#registerNewUserModal').modal('show');
            this.vModalUserId = dataJson.body.userId;
            this.vModalFistName = dataJson.body.username;
            this.vmodalInputPassword = dataJson.body.passwordHash;
            this.vModalEmail = dataJson.body.email;
            this.vmodalRoleId = dataJson.body.roleId;
            this.vmodalRoleName = dataJson.body.roleName;
            this.vmodalInputPhoneNumber = dataJson.body.phoneNumber;
            console.info(dataJson);


        },
        //dropdown search text
        selectItem(item) {
            this.vmodalRoleId = item.id;
            this.vmodalRoleName = item.name
            this.selectedItem = item;
            this.showDropdown = false;
        },
        async loadListOfUser() {
           this.vmInternal.table.ajax.reload(null, true);
            // var clientInfo = getClientInfo()
            // const requestJson = {
            //     requestId: clientInfo.uuid,
            //     type: "users",
            //     body: {}
            // };
            //
            // // Deserialize here
            // const dataJson = await fetchPOSTFull("/api/v1/registration/user/list", clientInfo, JSON.stringify(requestJson));
            // return dataJson.body.list.map(item => ({
            //     userId: item.userId,
            //     username: item.username,
            //     passwordHash: item.passwordHash,
            //     email: item.email,
            //     roleId: item.roleId,
            //     isActive: item.isActive,
            //     phoneNumber: item.phoneNumber,
            //     createdAt: item.createdAt,
            //     updatedAt: item.updatedAt
            // }));
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
        }
    }
};