import styled from "styled-components";
import Card from "../components/@common/Card";
import Text from "../components/@common/Text";
import InputBox from "../components/@common/InputBox";
import Button from "../components/@common/Button";

// 로그인 페이지
const LoginPage = () => {
  return (
    <StyledLoginPageWrapper>
      <StyledCardWrapper className="h-full">
        <div className=" flex items-center">
          <Card
            className="h-3/4 w-96 mr-20 drop-shadow-lg flex flex-col items-center"
            size="ExtraLarge"
            color="SkyBlue">
              <Text className="mt-20" size="text4" isBold={true}>로그인</Text>
              <Text className="mt-10 pe-40" size="text6">사용자 이름</Text>
              <InputBox className="w-60 mt-2"></InputBox>
              <Text className="mt-6 pe-44" size="text6">비밀번호</Text>
              <InputBox type="password" className="w-60 mt-2"></InputBox>
              <Button className="mt-8 w-60" size="Large" color="Main"><Text size="text6">로그인</Text></Button>
          </Card>
        </div>
      </StyledCardWrapper>
    </StyledLoginPageWrapper>
  );
};

export default LoginPage;

const StyledLoginPageWrapper = styled.div`
  width: 100%;
  height: calc(100vh - 60px);
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('/assets/login.png');
  background-size: cover; // 이미지가 div를 전체적으로 커버하도록 설정
  background-position: center; // 이미지가 중앙에 위치하도록 설정
  background-repeat: no-repeat;
`
const StyledCardWrapper = styled.div`
  width: 100%; // div가 부모의 전체 너비를 차지하도록 설정
  display: grid;
  justify-items: end; // grid 아이템들을 오른쪽으로 정렬
  
`;