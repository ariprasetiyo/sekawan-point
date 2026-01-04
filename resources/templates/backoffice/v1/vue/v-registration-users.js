const hostServer = "http://192.168.1.4:8080";

function formatDate(date) {
    const pad = (n) => n.toString().padStart(2, '0');

    const year = date.getFullYear();
    const month = pad(date.getMonth() + 1);
    const day = pad(date.getDate());
    const hour = pad(date.getHours());
    const minute = pad(date.getMinutes());
    const second = pad(date.getSeconds());

    return '${year}-${month}-${day} ${hour}:${minute}:${second}';
}

function generateUUIDv4() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function unauthorizedRedirect(responseRoleList) {
    // 🚨 Handle unauthorized / forbidden
   if (responseRoleList != null && responseRoleList.status != null && (responseRoleList.status === 401 || responseRoleList.status === 403)) {
        window.location.href = "/forbidden"; // or router push
        return;
    }

    // ❌ Other server errors
    if (responseRoleList != null && !responseRoleList.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
    }
}

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
                           <td><a class="btn btn-primary btn-user" id="{{user.userId}}" >edit</a> <a class="btn btn-primary btn-user">delete</a></td>
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
                  <a class="btn btn-secondary btn-user" data-dismiss="modal">
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
            listOfUser: [],
            vModalFistName: "",
            firstNameValid: false,
            vModalEmail: "",
            emailValid: false,
            vModalLastName: "",
            vmodalInputPhoneNumber: "+62",
            vmodalInputPassword: "",
            vmodalInputPasswordRepeat: "",
            passwordValid: false,
            phoneValid: false,
            passwordRepeatValid: false,
            //dropdown search text
            vmodalRoleName: "",
            vmodalRoleId: null,
            showDropdown: false,
            selectedItem: null,
            filterList: null,
            items: [{
                    id: 1,
                    name: "John Doe"
                },
                {
                    id: 2,
                    name: "Jane Smith"
                },
                {
                    id: 3,
                    name: "Michael Jordan"
                },
                {
                    id: 4,
                    name: "Lebron James"
                }
            ]
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
                    pageLength: 10,
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
            const regex = /^(?:\+62|62|0)8[1-9][0-9]{6,10}$/;
            this.phoneValid = regex.test(value);
        },
        vModalFistName(value) {
            const regex = /^[A-Za-zÀ-ÖØ-öø-ÿ\s]+$/;
            this.firstNameValid = regex.test(value);
        },
        vModalEmail(value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            this.emailValid = emailRegex.test(value);
        }

    },
    methods: {
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
/*

            var clientInfo = async getClientInfo();
            var responseListOfUser2 = null;
                        try {
                            responseListOfUser2 = await fetch(
                                "http://localhost:8080/api/v1/registration/user/save", {
                                    method: 'POST',
                                    headers: {
                                        'Accept': 'application/json',
                                        'x-request-id': clientInfo.uuid
                                    },
                                    body: JSON.stringify(requestJson)
                                }
                            );

                            unauthorizedRedirect(responseListOfUser2);
                            // Deserialize here
                            const dataJson = await responseListOfUser2.json();
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
                        } catch (err) {
                            unauthorizedRedirect(responseListOfUser2);
                            console.error("Failed to load users:", err);
                        }
*/

            this.vModalLastName;
            this.vModalEmail;
            this.vmodalInputPassword;
            this.vmodalInputPasswordRepeat;

            this.vmodalInputPhoneNumber;
            this.vmodalRoleId;
        },
        //dropdown search text
        selectItem(item) {
            this.vmodalRoleId = item.id;
            this.vmodalRoleName = item.name
            this.selectedItem = item;
            this.showDropdown = false;
        },
        getClientInfo() {

            const uuid = generateUUIDv4();
            const userAgent = navigator.userAgent;
            return {
                uuid,
                userAgent
            };
        },
        async loadListOfUser() {

            var clientInfo = this.getClientInfo()
            const requestJson = {
                requestId: clientInfo.uuid,
                type: "users",
                body: {}
            };

            var responseListOfUser = null;
            try {
                responseListOfUser = await fetch(
                    hostServer+"/api/v1/registration/user/list", {
                        method: 'POST',
                        credentials: 'include',
                        headers: {
                            'Accept': 'application/json',
                            'x-request-id':clientInfo.uuid
                        },
                        body: JSON.stringify(requestJson)
                    }
                );

                unauthorizedRedirect(responseListOfUser);
                // Deserialize here
                const dataJson = await responseListOfUser.json();
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
            } catch (err) {
                unauthorizedRedirect(responseListOfUser);
                console.error("Failed to load users:", err);
            }
            /*return [{
                    id: 1,
                    name: 'John Doe',
                    position: 'System Architect',
                    office: 'Edinburgh',
                    age: 61,
                    startDate: '2011/04/25',
                    salary: '$320,800'
                },
                {
                    id: 1,
                    name: 'Ari Prasetiyo',
                    position: 'BE Java Developer',
                    office: 'Edinburgh',
                    age: 61,
                    startDate: '2015/04/25',
                    salary: '$920,800'
                }
            ];*/
        },
        async loadUsers() {

            var clientInfo = this.getClientInfo()
            var responseRoleList = null;
            try {
                responseRoleList = await fetch(
                    hostServer+"/api/v1/registration/role/list", {
                        method: 'GET',
                        credentials: 'include',
                        headers: {
                            'Accept': 'application/json',
                            'x-request-id':  clientInfo.uuid
                            //                            'x-user-agent':  info.userAgent ,
                            //                            'Cookie': "--sas"
                        }
                    }
                );

                unauthorizedRedirect(responseRoleList);
                /*              console.info(dataJson)
                                console.info(document.cookie)*/

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

                //                console.info(this.items)

                // Map API response
                /*this.items = data.map(user => ({
                  id: user.id,
                  name: user.name
                }));*/
            } catch (err) {
                unauthorizedRedirect(responseRoleList);
                console.error("Failed to load users:", err);
            }
        },
    }
};