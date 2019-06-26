package com.example.gRpc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.sql.Druid;

import net.devh.examples.grpc.lib.UserGrpc;
import net.devh.examples.grpc.lib.UserInfo;
import net.devh.examples.grpc.lib.UserInfoList;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;

@GrpcService(UserGrpc.class)
public class GrpcUserServerService extends UserGrpc.UserImplBase {
	@Override
    public void add(net.devh.examples.grpc.lib.UserInfo request,
    		io.grpc.stub.StreamObserver<net.devh.examples.grpc.lib.UserInfoList> responseObserver) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = Druid.getConnection();			
			String sql = "insert into userinfo(id, name, age) values(?, ?, ?)";
			stmt = conn.prepareStatement(sql);	
			stmt.setInt(1, request.getId());         
			stmt.setString(2, request.getName());    
			stmt.setInt(3, request.getAge());  
			stmt.executeUpdate();
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {			 
			 Druid.release(conn,stmt,null);		    
		}
		find(request,responseObserver);
		
    }

	@Override
    public void remove(net.devh.examples.grpc.lib.UserInfo request, io.grpc.stub.StreamObserver<net.devh.examples.grpc.lib.UserInfoList> responseObserver) {
		Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Druid.getConnection();
            String sql = "delete from userinfo where id=?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, request.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
			 Druid.release(conn,stmt,null);		    
        }
        find(request, responseObserver);
    }

    @Override
    public void update(net.devh.examples.grpc.lib.UserInfo request, io.grpc.stub.StreamObserver<net.devh.examples.grpc.lib.UserInfoList> responseObserver) {
    	Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Druid.getConnection();
            String sql = "update userinfo set name=?,age=? where id=?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, request.getName());
            stmt.setInt(2, request.getAge());
            stmt.setInt(3, request.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
			 Druid.release(conn,stmt,null);		    
        }
        find(request, responseObserver);
    }
	

	@Override
    public void find(net.devh.examples.grpc.lib.UserInfo request,
    		io.grpc.stub.StreamObserver<net.devh.examples.grpc.lib.UserInfoList> responseObserver) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		UserInfoList.Builder userInfoList = UserInfoList.newBuilder();
		try {
			conn = Druid.getConnection();
			String sql = "select * from userinfo";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				int id  = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                UserInfo.Builder userInfo = UserInfo.newBuilder();
                userInfo.setId(id);
                userInfo.setAge(age);
                userInfo.setName(name);
                userInfoList.addUserInfo(userInfo);
	        }            
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			 Druid.release(conn,stmt,rs);		    
		}
		responseObserver.onNext(userInfoList.build());
	    responseObserver.onCompleted();
    }
	
	@Override
    public void findById(net.devh.examples.grpc.lib.UserInfo request,
    		io.grpc.stub.StreamObserver<net.devh.examples.grpc.lib.UserInfoList> responseObserver) {
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
		UserInfoList.Builder userInfoList = UserInfoList.newBuilder();
		try {
			conn = Druid.getConnection();
			String sql = "select * from userinfo where id=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, request.getId());
            rs = stmt.executeQuery();
			while(rs.next()){
				int id  = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                UserInfo.Builder userInfo = UserInfo.newBuilder();
                userInfo.setId(id);
                userInfo.setAge(age);
                userInfo.setName(name);
                userInfoList.addUserInfo(userInfo);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			 Druid.release(conn,stmt,rs);		    
		}
		responseObserver.onNext(userInfoList.build());
	    responseObserver.onCompleted();
    }


}
