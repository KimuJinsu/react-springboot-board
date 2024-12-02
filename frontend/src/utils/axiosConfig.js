import axios from "axios";

// Axios 인스턴스 생성
const instance = axios.create({
    baseURL: "http://localhost:8080/", // 기본 API URL (필요에 맞게 수정하세요)
    timeout: 5000, // 요청 타임아웃 설정 (밀리초 단위)
});

// Request Interceptor 설정
instance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("accessToken"); // 로컬 스토리지에서 토큰 가져오기
        if (token) {
            config.headers["Authorization"] = `Bearer ${token}`; // Authorization 헤더 추가
        }
        return config;
    },
    (error) => {
        return Promise.reject(error); // 요청 에러 처리
    }
);

// Response Interceptor 설정 (선택 사항)
instance.interceptors.response.use(
    (response) => response, // 응답 성공 시
    (error) => {
        // 응답 에러 처리 (예: 인증 만료 시)
        if (error.response && error.response.status === 401) {
            console.error("인증이 만료되었습니다. 다시 로그인하세요.");
        }
        return Promise.reject(error);
    }
);

export default instance; // 설정된 Axios 인스턴스를 내보내기