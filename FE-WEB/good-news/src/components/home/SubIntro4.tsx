import styled from "styled-components"
import Text from "../@common/Text"

//손전등 관련 설명
const SubIntro4 = () => {
    return(
        <StyledFlashLightPageWrapper>
            <StyledFlashLightIntro>
                <Text className="grid place-items-center" size="text1">나만의 긴급 손전등</Text>

                <div className="grid grid-cols-2">
                    <div className="relative">
                    

                        {/* S */}
                        <StyledShortImage className="absolute top-24 left-0" src="/assets/saveSShort.png"/>
                        <StyledShortImage className="absolute top-12 left-16" src="/assets/saveSShort.png"/>
                        <StyledShortImage className="absolute top-24 left-32" src="/assets/saveSShort.png"/>
                        <StyledWordImage className="absolute top-40 left-16" src="/assets/saveS.png"/>

                        {/* A */}
                        <StyledShortImage className="absolute top-32 left-48" src="/assets/saveAShort.png"/>
                        <StyledLongImage className="absolute top-32 right-64" src="/assets/saveALong.png"/>
                        <StyledWordImage className="absolute top-52 left-60" src="/assets/saveA.png"/>

                        {/* V */}
                        <StyledShortImage className="absolute top-32 right-44" src="/assets/saveVShort.png"/>
                        <StyledShortImage className="absolute top-48 right-28" src="/assets/saveVShort.png"/>
                        <StyledShortImage className="absolute top-1/3 right-6" src="/assets/saveVShort.png"/>
                        <StyledWordImage className="absolute top-72 right-28" src="/assets/saveV.png"/>

                        <div className="flex flex-col justify-end ms-12" style={{ height: 'calc(100vh - 160px)' }}>
                        <Text size="text4">근거리에 위치한</Text>
                        <Text size="text4">상대방과 소통하기 어려울 때</Text>
                            <Text size="text4"><strong>모스부호</strong>를 만들어 소통할 수 있어요</Text>
                        </div>
                        
                    </div>
                    <div className="relative">
                        <StyledFlashLightImage className="place-self-end" src="/assets/mainFlashLight.png"/>
                        {/* V */}
                        <StyledLongImage className="absolute top-1/3 left-4" src="/assets/saveVLong.png"/>
                        {/* E */}
                        <StyledShortImage className="absolute top-1/3 left-32" src="/assets/saveEShort.png"/>
                        <StyledWordImage className="absolute top-1/2 left-2" src="/assets/saveE.png"/>
                    </div>
                </div>

            </StyledFlashLightIntro>
        </StyledFlashLightPageWrapper>)
}

export default SubIntro4

const StyledFlashLightPageWrapper = styled.div`
width: 100%;
height: 100%;
`

const StyledFlashLightIntro = styled.div`
  width: 80%;
  height: 100%;
  margin: 0 auto;
`;

const StyledFlashLightImage = styled.img`
    height: 100%;
`

const StyledShortImage = styled.img`
    width: 6%;
    height: 6%;
`

const StyledLongImage = styled.img`
    width: 13%;
    height: 6%;
`

// const StyledWordImage = styled.img`
//     width: 8%;
//     height: 10%;
// `

const StyledWordImage = styled.img`
    width: 8%;
    height: 10%;

    // 반응형 스타일을 위한 미디어 쿼리
    // 화면 너비가 1024px 이하일 때
    @media (max-width: 1024px) {
        width: 8%;
        height: 10%;
    }

    // 화면 너비가 768px 이하일 때
    @media (max-width: 768px) {
        width: 6%;
        height: 8%;
    }

    // 화면 너비가 480px 이하일 때
    @media (max-width: 480px) {
        width: 4%;
        height: 5%;
    }
`;