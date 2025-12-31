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

export default {
    template: `
      <div class="d-sm-flex align-items-center justify-content-between mb-4">
         <h1 class="h3 mb-0 text-gray-800">Create User</h1>
         <a href="#" class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm"><i class="fas fa-download fa-sm text-white-50"></i> Generate Report</a>
      </div>
      <div class="card o-hidden border-0 shadow-lg my-3">
         <div class="card-body p-0">
            <!-- Nested Row within Card Body -->
            <div class="row">
               <!--<div class="col-lg-5 d-none d-lg-block bg-register-image"></div> -->
               <div class="col-lg-7">
                  <div class="p-5">
                     <!--
                        <div class="text-center">
                            <h1 class="h4 text-gray-900 mb-4">Create an Account!</h1>
                        </div>
                        -->
                     <form class="user">
                        <div class="form-group row">
                           <div class="col-sm-6 mb-3 mb-sm-0">
                              <input type="text" class="form-control form-control-user" id="exampleFirstName"
                                 placeholder="First Name">
                           </div>
                           <div class="col-sm-6">
                              <input type="text" class="form-control form-control-user" id="exampleLastName"
                                 placeholder="Last Name">
                           </div>
                        </div>
                        <div class="form-group">
                           <input type="email" class="form-control form-control-user" id="exampleInputEmail"
                              placeholder="Email Address">
                        </div>
                        <div class="form-group">
                           <input type="text" class="form-control form-control-user" id="inputPhoneNumber"
                              placeholder="Phone Number" ref="refInputPhoneNumber"
                              v-model="vmodalInputPhoneNumber"
                              :class="{'is-invalid': !phoneValid, 'is-valid': phoneValid}">
                        </div>
                        <div class="form-group row">
                           <div class="col-sm-6 mb-3 mb-sm-0">
                              <input type="password" class="form-control form-control-user" id="inputPassword"
                                 placeholder="Password" ref="refInputPassword"
                                 v-model="vmodalInputPassword"
                                 :class="{'is-invalid': !passwordValid, 'is-valid': passwordValid}">
                           </div>
                           <div class="col-sm-6">
                              <input type="password" class="form-control form-control-user"
                                 id="exampleRepeatPassword" placeholder="Repeat Password"
                                 :class="{'is-invalid': !passwordRepeatValid, 'is-valid': passwordRepeatValid}"
                                 v-model="vmodalInputPasswordRepeat">
                           </div>
                        </div>
                        <!-- dropdown search text -->
                        <div class="form-group">
                           <div class="dropdown">
                              <input type="text" class="form-control dropdown-toggle form-control-user" placeholder="Search role id ..." v-model="search"
                                 @focus="showDropdown = true" @input="filterList"/>
                              <div class="dropdown-menu show w-100" v-if="showDropdown">
                                 <a class="dropdown-item" v-for="item in filteredList" :key="item.id" @click="selectItem(item)">
                                 {{ item.name }}
                                 </a>
                                 <div v-if="filteredList.length === 0" class="dropdown-item text-muted">
                                    No result found
                                 </div>
                              </div>
                           </div>
                        </div>
                        <!-- dropdown search text end -->
                        <a class="btn btn-primary btn-user btn-block" @click="submitForm">
                        Register Account
                        </a>
                        <hr>
                        <a href="/backoffice/v1" class="btn btn-google btn-user btn-block">
                        <i class="fab fa-google fa-fw"></i> Register with Google
                        </a>
                        <a href="/backoffice/v1" class="btn btn-facebook btn-user btn-block">
                        <i class="fab fa-facebook-f fa-fw"></i> Register with Facebook
                        </a>
                     </form>
                     <hr>
                     <div class="text-center">
                        <a class="small" href="forgot-password.html">Forgot Password?</a>
                     </div>
                     <div class="text-center">
                        <a class="small" href="login.old">Already have an account? Login!</a>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
  `,
    data() {
        return {
            vmodalInputPhoneNumber: "+62",
            vmodalInputPassword: "",
            vmodalInputPasswordRepeat: "",
            passwordValid: false,
            phoneValid: false,
            passwordRepeatValid: false,
            //dropdown search text
            search: "",
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
    mounted() {
        this.loadUsers(); // fetch data on load
    },
    computed: {
        //dropdown search text
        filteredList() {
            return this.items.filter(item =>
                item.name.toLowerCase().includes(this.search.toLowerCase())
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
        }
    },
    methods: {
        getValue() {
            alert(this.$refs.refInputPhoneNumber.value);
        },
        showValue() {
            alert(this.vmodalInputPhoneNumber);
        },
        submitForm() {
            if (!this.phoneValid) {
                alert("Nomor HP tidak valid!");
                return;
            }

            alert("Nomor valid: " + this.phone);
        },
        //dropdown search text
        selectItem(item) {
            this.search = item.name;
            this.selectedItem = item;
            this.showDropdown = false;
        },
        async loadUsers() {
            try {
                const info = await getClientInfo();
                const res = await fetch(
                    "http://localhost:8080/api/v1/registration/role/list", {
                        method: 'GET',
                        headers: {
                            'Accept': 'application/json',
                            'x-request-id': info.uuid ,
                            'x-ip':  info.ip,
//                            'x-user-agent':  info.userAgent ,
//                            'Cookie': "--sas"
                        }
                    }
                );

/*              console.info(dataJson)
                console.info(document.cookie)*/

                // Deserialize here
                const dataJson = await res.json();
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
                console.error("Failed to load users:", err);
            }
        },
    }
};