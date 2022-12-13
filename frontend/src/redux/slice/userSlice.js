import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { getCurrentUserLoggedInByUsername } from '../../services/getApiService';

const initialState = {
  user: {
    username: '',
    location: '',
    role: '',
    accessToken: '',
  },
  isAuthenticated: false,
  isFirstLogin: true,
};

export const fetchUserLoggedIn = createAsyncThunk('users/fetchUserLoggedInById', async (username, thunkAPI) => {
  const response = await getCurrentUserLoggedInByUsername(username);
  return response.data;
});

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    fetchUserLoginSuccess: (state, action) => {
      state.user = {
        username: action.payload.username,
        location: action.payload.location,
        role: action.payload.role,
        accessToken: action.payload.accessToken,
      };
      state.isAuthenticated = true;
      state.isFirstLogin = action.payload.isFirstLogin;
    },
    updateFirstLogin: (state) => {
      state.isFirstLogin = false;
    },
    removeDataUserLogout: (state) => {
      state.user = {
        ...initialState.user,
      };
      state.isAuthenticated = initialState.isAuthenticated;
      state.isFirstLogin = initialState.isFirstLogin;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(fetchUserLoggedIn.fulfilled, (state, action) => {
      state.user = {
        ...state.user,
        username: action.payload.username,
        location: action.payload.location,
        role: action.payload.type,
      };
      state.isFirstLogin = action.payload.firstLogin;
    });
  },
});

export const { fetchUserLoginSuccess, removeDataUserLogout, updateFirstLogin } = userSlice.actions;

export default userSlice.reducer;
