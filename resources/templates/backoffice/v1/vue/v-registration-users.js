async function getClientInfo() {

    const uuid = crypto.randomUUID();
    const userAgent = navigator.userAgent;
    const response = await fetch("https://api.ipify.org?format=json");
    const ipData = await response.json();

    return {
        uuid,
        userAgent,
        ip: ipData.ip
    };
}

function unauthorizedRedirect(responseRoleList) {
    // 🚨 Handle unauthorized / forbidden
    if (responseRoleList.status === 401 || responseRoleList.status === 403) {
        window.location.href = "/forbidden"; // or router push
        return;
    }

    // ❌ Other server errors
    if (!responseRoleList.ok) {
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
            data-target="#registerModal"
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
                           <th>Name</th>
                           <th>Position</th>
                           <th>Office</th>
                           <th>Age</th>
                           <th>Start date</th>
                           <th>Salary</th>
                        </tr>
                     </thead>
                     <tfoot>
                        <tr>
                           <th>Name</th>
                           <th>Position</th>
                           <th>Office</th>
                           <th>Age</th>
                           <th>Start date</th>
                           <th>Salary</th>
                        </tr>
                     </tfoot>
                     <tbody>
                        <tr v-for="user in listOfUser" :key="user.id">
                           <td>{{ user.name }}</td>
                           <td>{{ user.position }}</td>
                           <td>{{ user.office }}</td>
                           <td>{{ user.age }}</td>
                           <td>{{ user.startDate }}</td>
                           <td>{{ user.salary }}</td>
                        </tr>
                     </tbody>
                  </table>
               </div>
            </div>
         </div>
      </div>

      <div
         class="modal fade"
         id="registerModal"
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
                  <a class="btn btn-primary btn-user" @click="submitForm">
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
            clientInfo: null,
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
        this.listOfUser = await this.loadListOfUser()
        this.$nextTick(() => {
                    $('#dataTable').DataTable();
                });
        this.clientInfo = await getClientInfo();
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
        async loadListOfUser() {
            return [{
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
            ];
        },
        getValue() {
            alert(this.$refs.refInputPhoneNumber.value);
        },
        showValue() {
            alert(this.vmodalInputPhoneNumber);
        },
        submitForm() {
            alert(this.vmodalRoleId)
            alert(this.vModalFistName);
            if (!this.phoneValid) {
                alert("Nomor HP tidak valid!");
                return;
            }

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
        async loadUsers() {
            var responseRoleList = null;
            try {

                responseRoleList = await fetch(
                    "http://localhost:8080/api/v1/registration/role/list", {
                        method: 'GET',
                        headers: {
                            'Accept': 'application/json',
                            'x-request-id': this.clientInfo.uuid,
                            'x-ip': this.clientInfo.ip,
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