import React from "react";
import defaultALT from '../../_image/defaultALT.png';
import axios from "axios";

const NoticeboardCommit = ({ data, delButton, idStats, postId }) => {
    // 현재 사용자의 username과 댓글 작성자의 username이 일치하는지 확인하여 삭제 옵션 설정
    const arrs = data.map(commit => ({
        ...commit,
        option: commit.username === idStats
    }));

    // 삭제 버튼 클릭 시 호출되는 함수
    const deleteCommit = async (commitId) => {
        if (!window.confirm("해당 댓글을 삭제하시겠습니까?")) {
            return;
        }

        try {
            await delButton(commitId); // delButton 함수가 비동기라면 await 사용
            alert("댓글이 삭제되었습니다.");
        } catch (e) {
            console.error("댓글 삭제 실패:", e);
            alert("댓글 삭제에 실패했습니다.");
        }
    };

    return (
        <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
            {arrs.map(commit => (
                <div
                    key={commit.id}
                    style={{
                        display: "flex",
                        alignItems: "flex-start",
                        padding: "10px",
                        border: "1px solid #ddd",
                        borderRadius: "5px",
                        position: "relative"
                    }}
                >
                    {/* 프로필 이미지 및 사용자 이름 */}
                    <span style={{ display: "flex", alignItems: "center", fontWeight: "bold", marginRight: "10px" }}>
                        <img
                            src={commit.profileImageUrl || defaultALT}
                            alt={`${commit.username}'s profile`}
                            style={{
                                width: "40px",
                                height: "40px",
                                borderRadius: "50%",
                                objectFit: "cover",
                                marginRight: "10px"
                            }}
                            onError={(e) => { e.target.onerror = null; e.target.src = defaultALT; }}
                        />
                        {commit.username}
                    </span>
                    
                    {/* 작성일자 아이콘 (Heroicons의 Calendar Icon) */}
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-6 w-6"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                        style={{ marginLeft: '10px', color: '#555' }}
                        width="16"
                        height="16"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                        />
                    </svg>
                    <span style={{ fontSize: "0.8em", color: "#666", marginLeft: "5px" }}>
                        {new Date(commit.createdAt).toLocaleString()}
                    </span>
                    
                    {/* 액션 버튼 컨테이너 */}
                    <div style={{ display: "flex", alignItems: "center", marginLeft: "auto" }}>
                        {commit.option && (
                            /* 삭제 아이콘 (Heroicons의 Trash Icon) */
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                onClick={() => deleteCommit(commit.id)}
                                className="h-6 w-6"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                                style={{ cursor: "pointer", marginLeft: "10px", color: 'red' }}
                                width="20"
                                height="20"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                                />
                            </svg>
                        )}
                    </div>
                    
                    {/* 댓글 내용 */}
                    <div style={{ flex: 1, textAlign: "left", marginLeft: "10px" }}>
                        <span>{commit.content}</span>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default React.memo(NoticeboardCommit);






// import React from "react";
// import defaultALT from '../../_image/defaultALT.png'
// import axios from "axios";

// const NoticeboardCommit = ({ data, delButton, idStats , postId }) => {
//     const arrs = data.map(postcommit => ({ ...postcommit, option: postcommit.writer === idStats }));

//     const reportContent = async (commit) => {
//         if(!window.confirm("해당 컨텐츠를 신고하시겠습니까?")) {
//             return;
//         }

//         const reason = window.prompt("신고 사유 (100자 이내)" + "");
//         if(reason.length > 100) {
//             alert("신고 사유는 100자 이내로 작성해주세요.");

//             return;
//         }

//         await axios({
//             method: "POST",
//             url: `/admin/report/content`,
//             data: {
//                 "content" : reason ,
//                 "reportType" : "COMMENT",
//                 "target" : {
//                     "writer" : commit.writer ,
//                     "content" : commit.content
//                 }
//             }
//         }).then((e) => {
//             alert("신고가 완료되었습니다.");
//         }).catch((e) => {
//             alert("로그인 후에 사용해주세요.");
//         });
//     }

//     return (
//         arrs.map(commit => (
//             <div className="userPostCommit" key={commit.commentId}>
//                 <span className='commitnickname'><img src={commit.writerImage || defaultALT} alt="img" className="profileImageData" />{commit.writerIsDelete ? "탈퇴한 사용자" : commit.writer}</span>
//                 <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-clock" viewBox="0 0 16 16">
//                     <path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z" />
//                     <path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z" />
//                 </svg>
//                 <span className='contentNumber'>{commit.postDate} </span>
//                 <svg style={{cursor: "pointer"}} xmlns="http://www.w3.org/2000/svg" onClick={() => reportContent(commit)} width="20" height="20" fill="currentColor" color="red" className="bi bi-bell" viewBox="0 0 16 16">
//                     <path style={{ fill: "rgb(255,0,0)" }} d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2zM8 1.918l-.797.161A4.002 4.002 0 0 0 4 6c0 .628-.134 2.197-.459 3.742-.16.767-.376 1.566-.663 2.258h10.244c-.287-.692-.502-1.49-.663-2.258C12.134 8.197 12 6.628 12 6a4.002 4.002 0 0 0-3.203-3.92L8 1.917zM14.22 12c.223.447.481.801.78 1H1c.299-.199.557-.553.78-1C2.68 10.2 3 6.88 3 6c0-2.42 1.72-4.44 4.005-4.901a1 1 0 1 1 1.99 0A5.002 5.002 0 0 1 13 6c0 .88.32 4.2 1.22 6z" />
//                 </svg>
//                 {commit.option ? <svg xmlns="http://www.w3.org/2000/svg" onClick={() => delButton(commit.commentId)} width="25" height="25" fill="currentColor" className="bi bi-trash deletecommit" viewBox="0 0 16 16">
//                     <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z" />
//                     <path fillRule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z" />
//                 </svg> : null}
//                 <div className='commitContent'>
//                     <span>{commit.content}</span>
//                 </div>
//             </div>
//         ))
//     )
// }

// export default React.memo(NoticeboardCommit);