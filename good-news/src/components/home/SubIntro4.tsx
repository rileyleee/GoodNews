import styled from "styled-components"
import Text from "../@common/Text"

//손전등 관련 설명
const SubIntro4 = () => {
    return(
        <StyledFlashLightPageWrapper>
            <StyledFlashLightIntro>
                <Text className="grid place-items-center" size="text1">나만의 긴급 손전등</Text>

                <div className="grid grid-cols-2">
                    <div className="flex flex-col justify-end ms-12" style={{ height: 'calc(100vh - 160px)' }}>
                       <Text size="text4">근거리에 위치한</Text>
                       <Text size="text4">상대방과 소통하기 어려울 때</Text>
                        <Text size="text4"><strong>모스부호</strong>를 만들어 소통할 수 있어요</Text>
                    </div>
                    <div className="">
                        <StyledFlashLightImage className="place-self-end mt-12" src="/assets/mainFlashLight.png"/>
                    </div>
                </div>

            </StyledFlashLightIntro>
        </StyledFlashLightPageWrapper>)
}

export default SubIntro4

const StyledFlashLightPageWrapper = styled.div`
  width: 100%;
  height: calc(100vh - 60px);
  display: flex;
  justify-content: center;
  align-items: center;
`

const StyledFlashLightIntro = styled.div`
  width: 80%;
  height: 100%;
  margin: 0 auto;
`;

const StyledFlashLightImage = styled.img`
    width: 100%;
    height: 100%;
`