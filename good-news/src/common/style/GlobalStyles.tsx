import { createGlobalStyle } from "styled-components";
export const GlobalStyles = createGlobalStyle`
    * {
        padding: 0;
        text-decoration: none;

        /* 기본 고정 폰트 지정 */
        font-family: 'Spoqa Han Sans Neo', 'sans-serif';
    }

    body {
        background-color: #E7ECEF;
    }

    /* 스크롤바 커스텀 */
    ::-webkit-scrollbar {
        width: 8px;
        background-color: #C0D6DF;
        border-radius: 16px;
    }

    ::-webkit-scrollbar-thumb {
        background-color: #274C77;
        border-radius: 16px;
    }
`;
